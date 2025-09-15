package com.guo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.Constants.SystemConstants;
import com.guo.domain.ResponseResult;
import com.guo.domain.entity.Comment;
import com.guo.domain.vo.CommentVo;
import com.guo.domain.vo.PageVo;
import com.guo.enums.AppHttpCodeEnum;
import com.guo.exception.SystemException;
import com.guo.mapper.CommentMapper;
import com.guo.service.CommentService;
import com.guo.service.UserService;
import com.guo.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-03-30 17:02:27
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private UserService userService;
    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对article进行判断
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);
        //根评论  rootId为-1
        queryWrapper.eq(Comment::getRootId,-1);

        queryWrapper.eq(Comment::getType,commentType);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        //分页查询
        Page<Comment> page = new Page(pageNum,pageSize);
        page(page,queryWrapper);

        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());

        //查询所有根评论对应的子评论集合，并且赋值给对应的属性
        for (CommentVo commentVo : commentVoList) {
            //查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());
            //赋值
            commentVo.setChildren(children);
        }

        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空
        if (!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        save(comment);
        return ResponseResult.okResult();
    }

    /**
     * 根据根评论id查询所对应子评论集合
     * @param id 根评论的id
     * @return
     */
    private List<CommentVo> getChildren(Long id){
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        List<CommentVo> commentVos = toCommentVoList(comments);
        Collections.reverse(commentVos);
        return commentVos;
    }

    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历Vo集合
        for (CommentVo commentVo : commentVos) {
            //通过createBy查询昵称并赋值
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            String avatar = userService.getById(commentVo.getCreateBy()).getAvatar();
            commentVo.setUsername(nickName);
            commentVo.setAvatar(avatar);
            //通过toCommentUserId查询昵称并赋值
            //如果toCommentUserId不为-1才查询
            if (commentVo.getToCommentId()!=-1){
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }
        return commentVos;
    }
}
