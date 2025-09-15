package com.guo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.Constants.SystemConstants;
import com.guo.domain.ResponseResult;
import com.guo.domain.dto.MenuDto;
import com.guo.domain.entity.Article;
import com.guo.domain.entity.Menu;
import com.guo.domain.vo.*;
import com.guo.enums.AppHttpCodeEnum;
import com.guo.mapper.MenuMapper;
import com.guo.service.MenuService;
import com.guo.utils.BeanCopyUtils;
import com.guo.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-04-03 21:13:38
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;
    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是管理员返回所有权限
        if (SecurityUtils.isAdmin()){

            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType,SystemConstants.MENU,SystemConstants.BUTTON);
            wrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回其所具有的权限
        return menuMapper.selectPermsByUserId(id);
    }

    @Override
    public List<MenuVo> selectRouterMenuTreeByUserId(Long userId) {

        MenuMapper menumapper = getBaseMapper();
        List<MenuVo> menus = null;

        //判断是否是管理员
        if (SecurityUtils.isAdmin()){
            //如果是返回所有符合的menu
            menus = menumapper.selectAllRouter();
        }else {
            //查询对应的menu
            menus = menumapper.selectRouterMenuTreeByUserId(userId);
        }
        //构建tree

        //先找出第一层菜单，然后再找子菜单，设置到children中
        List<MenuVo> menuTree = buildMenuTree(menus,0L);
        return menuTree;
    }

    @Override
    public ResponseResult list(MenuDto menuDto) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(menuDto.getStatus()),Menu::getStatus,menuDto.getStatus());
        queryWrapper.like(StringUtils.hasText(menuDto.getMenuName()),Menu::getMenuName,menuDto.getMenuName());
        queryWrapper.orderByAsc(Menu::getParentId);
        queryWrapper.orderByAsc(Menu::getOrderNum);
        List<Menu> list = list(queryWrapper);
        //封装数据返回
        //封装成tagVo集合
        List<MenuListVo> menuListVos = BeanCopyUtils.copyBeanList(list, MenuListVo.class);

        return ResponseResult.okResult(menuListVos);
    }

    @Override
    public ResponseResult addMenu(MenuListVo menuListVo) {
        Menu menu = BeanCopyUtils.copyBean(menuListVo, Menu.class);
        save(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMenu(Long id) {
        Menu menu = getById(id);
        MenuListVo menuListVo = BeanCopyUtils.copyBean(menu, MenuListVo.class);
        return ResponseResult.okResult(menuListVo);
    }

    @Override
    public ResponseResult updateMenu(MenuListVo menuListVo) {
        Menu menu = BeanCopyUtils.copyBean(menuListVo, Menu.class);
        Menu menu1 = menuMapper.selectById(menuListVo.getId());
        if (Objects.equals(menu.getParentId(), menu1.getId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"修改菜单'写博文'失败，上级菜单不能选择自己");
        }
        updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,menuId);
        List<Menu> list = list(queryWrapper);
        if (list.size()!=0){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"存在子菜单不允许删除");
        }
        removeById(menuId);
        return ResponseResult.okResult();
    }

    private List<MenuVo> buildMenuTree(List<MenuVo> menus, Long parentId) {
        List<MenuVo> muenTree = menus.stream()
                .filter(menuVo -> menuVo.getParentId().equals(parentId))
                .map(menuVo -> menuVo.setChildren(getChildren(menuVo, menus)))
                .collect(Collectors.toList());
        return muenTree;
    }


    /**
     * 获取传入参数的子menu集合
     *
     * @param menuVo
     * @param menus
     * @return
     */
    private List<MenuVo> getChildren(MenuVo menuVo, List<MenuVo> menus) {
        List<MenuVo> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menuVo.getId()))
                .map(m ->m.setChildren(getChildren(m,menus)))
                .collect(Collectors.toList());
        return childrenList;
    }
}
