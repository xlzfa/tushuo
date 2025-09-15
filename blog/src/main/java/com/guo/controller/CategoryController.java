package com.guo.controller;

import com.guo.domain.ResponseResult;
import com.guo.domain.vo.AllCategoryVo;
import com.guo.service.CategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author guo
 * @Date 2023 03 26 20 14
 **/

@RestController
@RequestMapping("/category")
@Api(tags = "分类",description = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/getCategoryList")
    public ResponseResult getCategoryList(){
        return categoryService.getCategoryList();
    }

    @GetMapping("/getHotCategory")
    public ResponseResult getHotCategory(){
        return categoryService.getHotCategory();
    }

    @GetMapping("/listAllCategory")
    public ResponseResult getListAllCategory(){
        List<AllCategoryVo> list = categoryService.getListAllCategory();
        return ResponseResult.okResult(list);
    }
}
