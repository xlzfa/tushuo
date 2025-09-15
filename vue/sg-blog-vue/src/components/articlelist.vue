<template>
    <div class="article-category">
        <div class="article-item" v-for="item in articleList" :key="item.id">
            <div class="article-content">
            <img :src="item.thumbnail" :alt="item.title" class="article-thumbnail">
            <div class="article-text">
                <h2 class="article-title">
                <a :href="'#/DetailArticle?aid=' + item.id" target="_blank">
                    {{ item.title }}
                </a>
                </h2>
                <!-- <div class="category-name">
                    <a :href="'#/Share?classId='+item.categoryId" style="color: #fff; font-weight: bold;">{{item.categoryName}}</a>
                </div> -->
                
                <p class="article-summary">
                    {{ item.summary }}
                </p>  
                <p class="article-date">{{ showInitDate(item.createTime, 'all') }}
                </p>
                
            </div>
            </div>
        </div>
        <div class="text-center" v-if="hasMore">
            <a href="javascript:void(0);" class="btn btn-primary" @click="addMoreFun">Load More</a>
        </div>
    </div>
</template>


<script>
import {initDate} from '../utils/server.js'
import {articleList} from '../api/article'
    export default {
        name:'Share',
        data() { //选项 / 数据
            return {
                // 查询参数
                queryParams: {
                    pageNum: 1,
                    pageSize: 10,
                    categoryId: 0
                },
                articleList:[],
                hasMore:true
            }
        },

        methods: { //事件处理器
            showInitDate: function(oldDate,full){
                return initDate(oldDate,full)
            },
            getList() {
  articleList(this.queryParams).then((response) => {
    this.articleList = this.articleList.concat(response.rows);
    if (response.total <= this.articleList.length) {
      this.hasMore = false;
    } else {
      this.queryParams.pageNum++;
      this.getList(); // 递归继续加载下一页
    }
  });
}
,
            showSearchShowList:function(initData){//展示数据
                if(initData){
                    this.articleList = []

                }
                this.getList()
            },
            addMoreFun:function(){//查看更多
                this.showSearchShowList(false);
            },
            routeChange:function(){
                var that = this;

                // 判断路径是否以 /Search 开头
    if (that.$route.path.startsWith('/Search')) {
        const saved = localStorage.getItem('searchArticles');
        if (saved) {
            that.articleList = JSON.parse(saved);
            that.hasMore = false; // 禁用“查看更多”按钮
            return; // 提前退出，不再请求后端接口
        }
    }
                
                this.queryParams.categoryId = (that.$route.query.classId==undefined?0:parseInt(that.$route.query.classId));//获取传参的classId
                this.showSearchShowList(true);
            }
        },
        components: { //定义组件

        },
        watch: {
           // 如果路由有变化，会再次执行该方法
           '$route':'routeChange',
           '$store.state.keywords':'routeChange'
         },
        created() { //生命周期函数
            // console.log(this.$route);
            var that = this;
            that.routeChange();
        }
    }
</script>


<style scoped>
.article-category {
    background-color: #fff;
}

.article-item {
  margin-bottom: 20px;
  border-bottom: 0.5px solid #f5f5f5;
  padding-bottom: 20px;
  display: flex;
  align-items: flex-start;
}

.article-content {
  display: flex;
  width: 100%;
}

.article-thumbnail {
  max-width: 200px; /* 调整图片最大宽度 */
  max-height: 150px; /* 调整图片最大高度 */
  height: auto;
  margin-left: 20px;
  margin-right: 20px;
  border-radius: 8px;
  object-fit: cover; /* 保持图片比例 */
}

.article-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.article-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 10px;
}

.article-title a {
  color: #333;
  text-decoration: none;
}

.article-title a:hover {
  color: #007bff;
}

.article-date {
  font-size: 14px;
  color: #666;
  margin: 10px 0 0; /* 确保日期在底部 */
}

.article-summary {
  margin-top: 5px;
}

.category-name {

    width: 50px;
  background-color: #000; /* 背景颜色为黑色 */
  padding: 2px 5px; /* 内边距 */
  border-radius: 4px; /* 圆角边框 */
  font-weight: bold; /* 字体加粗 */
  margin-bottom: 10px; /* 与下一个元素的间距 */
  
}

</style>





