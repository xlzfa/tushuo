package com.guo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.Constants.SystemConstants;
import com.guo.domain.ResponseResult;
import com.guo.domain.dto.AddArticleDto;
import com.guo.domain.dto.ArticleListDto;
import com.guo.domain.entity.Article;
import com.guo.domain.entity.ArticleTag;
import com.guo.domain.entity.Category;
import com.guo.domain.entity.User;
import com.guo.domain.vo.*;
import com.guo.mapper.ArticleMapper;
import com.guo.mapper.UserMapper;
import com.guo.service.ArticleService;
import com.guo.service.ArticleTagService;
import com.guo.service.CategoryService;
import com.guo.utils.BeanCopyUtils;
import com.guo.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author guo
 * @Date 2023 03 24 16 56
 **/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    @Lazy
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public ResponseResult hotArticleList() {
        // 从 Redis 中获取所有文章的浏览量
        Map<String, Integer> viewCountMap = redisCache.getCacheMap("article:viewCount");

        // 将 Redis 中的浏览量转换为一个列表，用于排序
        List<Map.Entry<Long, Integer>> viewCountList = viewCountMap.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(Long.parseLong(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        // 按照浏览量降序排序
        viewCountList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // 获取排序后的前10篇文章的ID
        List<Long> topArticleIds = viewCountList.stream()
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 根据文章ID查询文章详情
        List<Article> articles = new ArrayList<>();
        for (Long id : topArticleIds) {
            Article article = getById(id);
            if (article != null && Integer.valueOf(article.getStatus()).equals(SystemConstants.ARTICLE_STATUS_NORMAL)) {
                // 设置浏览量
                article.setViewCount(viewCountMap.get(id.toString()).longValue());
                articles.add(article);
            }
        }

        // Bean拷贝
        List<HotArticleVo> articleVos = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);

        return ResponseResult.okResult(articleVos);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        // 构造查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);

        // 按创建时间降序排列（最新的文章在最前面）
        lambdaQueryWrapper.orderByDesc(Article::getCreateTime);

        // 查询所有符合条件的文章（不分页）
        List<Article> articles = articleMapper.selectList(lambdaQueryWrapper);

        // 设置分类名
        articles.forEach(article -> {
            Category category = categoryService.getById(article.getCategoryId());
            article.setCategoryName(category.getName());
        });

        // 转换为VO对象
        List<ArticleDetailVo> articleListVos = BeanCopyUtils.copyBeanList(articles, ArticleDetailVo.class);

        // 封装结果
        PageVo pageVo = new PageVo(articleListVos, (long) articleListVos.size());
        return ResponseResult.okResult(pageVo);
    }


    @Override
    public ResponseResult searchArticle(Integer pageNum, Integer pageSize, String title) {
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 按标题模糊查询
        lambdaQueryWrapper.like(Objects.nonNull(title) && !title.isEmpty(), Article::getTitle, title);

        // 状态是正式发布
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);

        // 置顶排序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        // 分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, lambdaQueryWrapper);

        List<Article> articles = page.getRecords();

        // 设置分类名
        articles.forEach(article -> {
            Category category = categoryService.getById(article.getCategoryId());
            article.setCategoryName(category != null ? category.getName() : null);
        });

        // 封装为 VO
        List<ArticleDetailVo> articleListVos = BeanCopyUtils.copyBeanList(articles, ArticleDetailVo.class);
        Collections.reverse(articleListVos); // 如有需要

        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }




    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        Long createBy = article.getCreateBy();
        User user = userMapper.selectById(createBy);
        //从redis中获取viewCount
//        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
//        article.setViewCount(viewCount.longValue());

        //封装查询结果
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        articleDetailVo.setNickName(user.getNickName());
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应ID的浏览量
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        syncViewCountToDatabase(id);
        return ResponseResult.okResult();
    }

    /**
     * 将 Redis 中的浏览量同步到 MySQL 数据库
     * @param id 文章 ID
     */
    private void syncViewCountToDatabase(Long id) {
        // 从 Redis 中获取当前文章的浏览量
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        long viewCountLong = viewCount.longValue();
        if (viewCount != null) {
            // 更新 MySQL 中的浏览量
            articleMapper.updateViewCountToMysql(id,viewCountLong);
        }
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);

        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult pageArticleList(Integer pageNum, Integer pageSize, ArticleListDto articleListDto) {
        //分页查询
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(articleListDto.getTitle()),Article::getTitle,articleListDto.getTitle());
        queryWrapper.like(StringUtils.hasText(articleListDto.getSummary()),Article::getSummary,articleListDto.getSummary());

        Page<Article> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        Page<Article> articleList = page(page, queryWrapper);
        //封装数据返回
        //封装成tagVo集合
        List<ArticleVo> ArticleVo = BeanCopyUtils.copyBeanList(articleList.getRecords(), ArticleVo.class);
        PageVo  pageVo = new PageVo(ArticleVo,articleList.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticle(Long id) {
        Article article = getBaseMapper().selectById(id);
//        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ArticleTag::getArticleId,id);
//        queryWrapper.select(ArticleTag::getTagId);
//        List<ArticleTag> list = articleTagService.list(queryWrapper);
//        List<Long> longList = new ArrayList<>();
//        for (ArticleTag articleTag : list) {
//            Long tagId = articleTag.getTagId();
//            longList.add(tagId);
//        }
        List<Long> tagList = articleTagService.getTagList(article.getId());
        GetArticleVo getArticleVo = BeanCopyUtils.copyBean(article, GetArticleVo.class);
        getArticleVo.setTags(tagList);
        return ResponseResult.okResult(getArticleVo);
    }

    @Override
    public ResponseResult update(GetArticleVo getArticleVo) {
        Article article = BeanCopyUtils.copyBean(getArticleVo, Article.class);
        updateById(article);
        //更新前的tag集合
        List<Long> tagList = articleTagService.getTagList(getArticleVo.getId());
        //getArticleVo.getTags()更新后的tag集合
        List<ArticleTag> articleTags = getArticleVo.getTags().stream()
                .filter(tagId->!tagList.contains(tagId))
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());
                if (articleTags.size()>0){
                    articleTagService.saveBatch(articleTags);
                }else if (articleTags.size()==0){
                    List<ArticleTag> articleTags2 = tagList.stream()
                            .filter(tagId->!getArticleVo.getTags().contains(tagId))
                            .map(tagId -> new ArticleTag(article.getId(), tagId))
                            .collect(Collectors.toList());
                    for (ArticleTag articleTag : articleTags2) {
                        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(ArticleTag::getArticleId,getArticleVo.getId());
                        queryWrapper.eq(ArticleTag::getTagId,articleTag.getTagId());
                        articleTagService.remove(queryWrapper);
                    }
                }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteArticle(Long id) {
        /**
         * 删除文章
         */
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, id);
        remove(queryWrapper);

        /**
         * 接触文章对应的标签关系
         */
        //获取tag集合
        LambdaQueryWrapper<ArticleTag> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ArticleTag::getArticleId,id);
        List<ArticleTag> list = articleTagService.list(queryWrapper1);
        for (ArticleTag articleTag : list) {
            LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ArticleTag::getArticleId,articleTag.getArticleId());
            wrapper.eq(ArticleTag::getTagId,articleTag.getTagId());
            articleTagService.remove(wrapper);
        }
        return ResponseResult.okResult();
    }
}