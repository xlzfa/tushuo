package com.guo.controller;

import com.guo.domain.ResponseResult;
import com.guo.domain.dto.AddArticleDto;
import com.guo.service.ArticleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author guo
 * @Date 2023 03 24 17 01
 **/
@RestController
@RequestMapping("/article")
@Api(tags = "文章",description = "文章相关接口")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    /**
     * 增加文章
     * @param articleDto
     * @return
     */
    @PostMapping("/add")
    public ResponseResult add(@RequestBody AddArticleDto articleDto){

        return articleService.add(articleDto);
    }


    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList(){
        ResponseResult result = articleService.hotArticleList();
        return result;
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum,Integer pageSize, Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }

    @GetMapping("/searchArticle")
    public ResponseResult searchArticle(Integer pageNum, Integer pageSize, String title) {
        return articleService.searchArticle(pageNum, pageSize, title);
    }


    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }

    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") Long id){
        return articleService.deleteArticle(id);
    }
}
