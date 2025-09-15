<!-- 右侧固定导航栏 -->
<template>
  <div class="rightlistBox">
    <div class="search-container">
      <el-input
        v-model="searchText"
        placeholder="请输入文章标题"
        class="search-input"
        clearable
        @keyup.enter.native="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch"><i class="fa fa-wa fa-search"></i></el-button>
        </template>
      </el-input>

      <!-- <div class="article-list">
        <el-card
          v-for="article in articles"
          :key="article.id"
          class="article-card"
          shadow="hover"
        >
          <h3>{{ article.title }}</h3>
          <p style="color: #888;">分类：{{ article.categoryName }}</p>
        </el-card>
      </div> -->
    </div>
<section class="rs4">
      <h2 class="">大家在看</h2>
      <ul>
        <li v-for="(item, index) in browseList" :key="'browseList' + index">
          <a :href="'#/DetailArticle?aid=' + item.id" target="_blank">{{ item.title }}</a>
          —— {{ item.viewCount }} 次围观
        </li>
      </ul>
    </section>
    <section>
      <div class="rs4">
        <h2 class="">当前热搜</h2>
        <ul>
          <li v-for="(item, index) in hotCategory" :key="'hotCategory' + index">
            <a :href="'#/Share?classId=' + item.id" target="_blank">{{ item.name }}</a>
            —— 包含 {{ item.articleCount }} 篇文章
          </li>
        </ul>
      </div>
    </section>

    

    <!-- 右侧上滑小图片 -->
    <div
      v-if="this.$store.state.themeObj.user_start != 0"
      :class="gotoTop ? 'toTop hidden' : 'toTop goTop hidden'"
      @click="toTopfun"
    >
      <img
        :src="
          this.$store.state.themeObj.right_img
            ? this.$store.state.themeObj.right_img
            : 'static/img/scroll.png'
        "
        alt=""
      />
    </div>
    <div
      v-else
      :class="gotoTop ? 'toTophui hidden' : 'toTophui goTophui hidden'"
      @click="toTopfun"
    >
      <img
        :src="
          this.$store.state.themeObj.right_img
            ? this.$store.state.themeObj.right_img
            : 'static/img/scroll.png'
        "
        alt=""
      />
    </div>
  </div>
</template>

<script>
import { hotArticleList, searchArticle } from "../api/article";
import { getHotCategory } from "../api/category";

export default {
  data() {
    return {
      fixDo: false,
      loveme: false,
      gotoTop: false,
      going: false,
      browseList: [],  // 修改为数组，便于处理API返回的数据
      hotCategory: [], // 修改为数组，便于处理API返回的数据
      artCommentList: "",
      searchText: "",        // 搜索框绑定
      articles: [],          // 搜索结果列表
      catchMeObj: {},
      pageNum: 1,            // 默认第一页
      pageSize: 10,          // 默认每页10条
    };
  },
  methods: {
    // 返回顶部功能
    toTopfun() {
      this.gotoTop = false;
      this.going = true;
      const timer = setInterval(() => {
        const osTop = document.documentElement.scrollTop || document.body.scrollTop;
        const ispeed = Math.floor(-osTop / 7);
        document.documentElement.scrollTop = document.body.scrollTop = osTop + ispeed;
        if (osTop === 0) {
          this.going = false;
          clearInterval(timer);
        }
      }, 30);
    },
    // 跳转到写作页面
    write() {
      this.$router.push('/write');
    },
    // 获取热门文章列表
    getHotArticleList() {
      hotArticleList().then((response) => {
        this.browseList = response || [];  // 确保即使没有数据，也不会出错
      }).catch(error => {
        console.error("获取热门文章失败", error);
        this.browseList = [];
      });
    },
    // 获取热门分类列表
    getHotCategoryList() {
      getHotCategory().then((response) => {
        // 过滤掉没有 articleCount 或为 NaN 的分类
        this.hotCategory = response.filter(item => !isNaN(item.articleCount) && item.articleCount !== null);
      }).catch(error => {
        console.error("获取热门分类失败", error);
        this.hotCategory = [];
      });
    },
    // 搜索功能
    handleSearch() {
      if (!this.searchText.trim()) {
        this.articles = []; // 如果搜索框为空，清空搜索结果
        return;
      }

      const query = {
        title: this.searchText,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      };

      searchArticle(query).then(response => {
        this.articles = response.rows || [];  // 确保返回的 rows 有效

        // 将搜索结果存储到 localStorage
        localStorage.setItem('searchArticles', JSON.stringify(this.articles));

        // 跳转到 Search 页面
        this.$router.push({ name: 'Search' });

      }).catch(error => {
        console.error("搜索失败", error);
        this.articles = [];
      });
    }
  },
  created() {
    // 监听页面滚动
    window.onscroll = () => {
      const t = document.documentElement.scrollTop || document.body.scrollTop;
      if (!this.going) {
        this.gotoTop = t > 600; // 超过600px显示返回顶部按钮
      }
      this.fixDo = t > 1200;  // 超过1200px时，显示其他元素或效果
    };

    // 初始化获取热门文章和分类
    this.getHotArticleList();
    this.getHotCategoryList();
  }
};
</script>



<style lang="less">
.rightlistBox {
  position: relative;
}
.rightlistBox section {
  transition: all 0.2s linear;
  position: relative;
  background: #fff;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 5px;
}
.rightlistBox section:hover {
  transform: translate(0, -2px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}
.rightlistBox .r1-head {
  text-align: center;
  border-radius: 4px 4px 0 0;
  text-align: center;
  position: relative;
  /*box-shadow: inset 0 -70px 100px -50px rgba(0,0,0,.5);*/
}
.rightlistBox .r1-head img {
  width: 100%;
  min-height: 100px;
}
.rightlistBox .r1-head h1 {
  position: absolute;
  bottom: 5px;
  margin: 0 0 0 -65px;
  font-size: 20px;
  letter-spacing: 0.5px;
  color: #fff;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.7);
  font-weight: 700;
  width: 130px;
  left: 50%;
}
.rightlistBox .r1-head h1 span {
  opacity: 0.3;
}
.rightlistBox .r1-body p {
  font-size: 14px;
  margin: 5px 0 8px 0;
  font-weight: 700;
  text-align: center;
}
.rightlistBox .r1-body .catch-me {
  text-align: center;
}
.rightlistBox .r1-body .catch-me a {
  display: inline-block;
  margin-bottom: 7px;
  position: relative;
  text-decoration: none;
}
.rightlistBox .r1-body .catch-me a:hover i {
  color: #fff;
  background: #f4692c;
}
.rightlistBox .r1-body .catch-me a i {
  display: inline-block;
  font-size: 18px;
  width: 42px;
  height: 42px;
  line-height: 42px;
  border-radius: 42px;
  color: rgba(0, 0, 0, 0.5);
  background: rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease-in-out;
  font-style: normal;
  margin: 0 3.2px;
}

/*************do you like me*******************/
.rightlistBox .rs2 {
  /*padding:10px 0 4px 0;*/
  min-height: 100px;
}
.rightlistBox .rs2.fixed {
  position: fixed;
  top: 40px;
  width: 22%;
}
.rightlistBox .rs2 p {
  color: #df2050;
  cursor: pointer;
  font-size: 20px;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
  margin-top: 10px;
  font-weight: 500;
}
.rightlistBox .rs2 div {
  color: #df2050;
  cursor: pointer;
  text-align: center;
  font-size: 40px;
  position: absolute;
  width: 100%;
  height: 100px;
  line-height: 100px;
  left: 0;
  top: 30px;
}
.rightlistBox .rs2 div i.heart {
  display: inline-block;
  text-align: center;
  width: 100px;
  height: 100px;
  margin-left: -20px;
  margin-top: -5px;
  background: url(../../static/img/heart.png) no-repeat;
  background-position: 0 0;
  cursor: pointer;
  -webkit-transition: background-position 1s steps(28);
  transition: background-position 1s steps(28);
  -webkit-transition-duration: 0s;
  transition-duration: 0s;
  vertical-align: middle;
}
.rightlistBox .rs2 div i.heart:hover {
  transform: scale(1.15);
  -webkit-transform: scale(1.15);
}
.rightlistBox .rs2 div i.heart.active {
  -webkit-transition-duration: 1s;
  transition-duration: 1s;
  background-position: -2800px 0;
}
.rightlistBox .rs2 div span {
  margin-left: -30px;
}
/**********排队来说*************/
.rightlistBox .rs3 .rs3-item {
  font-size: 13px;
  line-height: 20px;
}
.rightlistBox .rs3 .rs3-item a {
  display: block;
  padding: 5px;
  transition: all 0.3s ease-out;
  border-bottom: 1px solid #ddd;
  margin: 5px 0;
}
.rightlistBox .rs3 .rs3-item a:hover {
  background: rgba(230, 244, 250, 0.5);
  border-radius: 5px;
}
.rightlistBox .rs3 .rs3-photo {
  float: left;
}
.rightlistBox .rs3 .rs3-photo img {
  border-radius: 50%;
  width: 32px;
  height: 32px;
  object-fit: cover;
}
.rightlistBox .rs3 .rs3-inner {
  margin-left: 40px;
}
.rightlistBox .rs3 .rs3-inner .rs3-author {
  font-weight: 700;
}
.rightlistBox .rs3 .rs3-inner .rs3-text {
  font-size: 12px;
  text-align: justify;
}
.rightlistBox .rs3 .rs3-item:last-child a {
  border-bottom: none;
}
/************排队看这些***************/
.rightlistBox .rs4 li {
  padding: 8px 0;
  text-align: justify;
}
.rightlistBox .rs4 li a {
  font-weight: 600;
}
.rightlistBox .rs4 li a:hover {
  color: #64609e;
}

/*回到顶部*/
/*返回到顶部*/
.toTop {
  position: fixed;
  right: 40px;
  top: -150px;
  z-index: 99;
  width: 70px;
  height: 900px;
  transition: all 0.5s 0.3s ease-in-out;
  cursor: pointer;
}
.goTop {
  top: -950px;
}
.toTop img,
.toTophui img {
  width: 100%;
  height: auto;
}
.toTophui {
  position: fixed;
  right: 10px;
  bottom: 80px;
  z-index: 99;
  width: 120px;
  height: 120px;
  transition: all 0.5s 0.3s ease-in-out;
  cursor: pointer;
  animation: toflow 2s ease-in-out infinite;
}
@keyframes toflow {
  0% {
    /*top:400px;*/
    transform: scale(0.95) translate(0, 10px);
  }
  50% {
    /*top:410px;*/
    transform: scale(1) translate(0, 0px);
  }
  100% {
    /*top:400px;*/
    transform: scale(0.95) translate(0, 10px);
  }
}
.goTophui {
  bottom: 120vh;
}

.search-container {
  background: #fff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  max-width: 800px;
  margin: 10px auto;
}

.search-input {
  width: 100%;
  border-radius: 8px;
  --el-input-bg-color: #ffffff;
  --el-input-hover-border: #dcdfe6;
}

.article-list {
  margin-top: 2px;
  display: grid;
  gap: 16px;
}

.article-card {
  border-radius: 10px;
  transition: all 0.3s;
}

.article-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
}
</style>
