package com.guo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guo.domain.dto.CategoryCountDto;
import com.guo.domain.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    int updateViewCountToMysql(@Param("id") Long id, @Param("viewCount") Long viewCount);

    @Select("SELECT category_id AS categoryId, COUNT(*) AS count FROM sg_article WHERE status = 0 GROUP BY category_id ORDER BY count DESC LIMIT 10")
    List<CategoryCountDto> selectTopCategoryIdsByArticleCount();



}
