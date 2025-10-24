package com.guo.job;

import com.guo.domain.entity.Article;
import com.guo.service.ArticleService;
import com.guo.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;
//TODO view_count为空

    @Scheduled(cron = "0 0/1 * * * ?")
    public void UpdateViewCount() {
        // 1. Redis 里目前是 <String,Integer>
        Map<String, Integer> Map = redisCache.getCacheMap("article:viewCount");
        if (Map == null || Map.isEmpty()) {
            return;
        }

        // 2. 转 List<Article>
        List<Article> articles = Map.entrySet()
                .stream()
                .map(entry -> new Article(
                        Long.valueOf(entry.getKey()),                      // id
                        entry.getValue() == null ? 0L : entry.getValue().longValue()))
                .collect(Collectors.toList());

        // 3. 批量写库
        articleService.updateBatchById(articles);

        // 4. 清零（仍然保持原逻辑，写 0）
//        Map.keySet().forEach(k -> redisCache.setCacheMapValue("article:viewCount:delta", k, 0));
    }



//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void UpdateViewCount(){
//        //获取redis中的浏览量，更新到数据库中
//        //Map<String, Integer> viewCountMap = redisCache.getCacheMap("article:viewCount");
//        Map<String, Long> deltaMap = redisCache.getCacheMap("article:viewCount:delta");
//        if (deltaMap.isEmpty()) {
//            return;
//        }
//        List<Article> articles = deltaMap.entrySet()
//                .stream()
//                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue() == null ? 0L : entry.getValue().longValue()))
//                .collect(Collectors.toList());
//        //更新到数据库中
//        articleService.updateBatchById(articles);
//        // 写完一次性把 delta 清 0
//        deltaMap.keySet().forEach(k -> redisCache.setCacheMapValue("article:viewCount:delta", k, "0"));
//    }

}
