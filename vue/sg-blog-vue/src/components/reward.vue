<template>
  <div class="featured-posts">
    <h2 class="title">分类</h2>
    <div class="card-container">
      <div
        class="card"
        v-for="(post, index) in posts"
        :key="index"
        @click="goToCategory(post.categoryId)"
        style="cursor: pointer;"
      >
        <img :src="post.image" alt="post image" class="card-image" />
        <div class="card-content">
          <div class="card-meta">
            <span class="tag">{{ post.tag }}</span>
          </div>
          <p class="card-description">{{ post.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FeaturedPosts",
  data() {
    return {
      posts: [
        {
          image: "/static/img/category/bingxue.jpeg",
          tag: "冰雪",
          description: "银装素裹，体验寒冬中纯净与宁静。",
          categoryId: 22
        },
        {
          image: "/static/img/category/chengshi.jpeg",
          tag: "城市",
          description: "繁华都市，记录人文与现代的交融。",
          categoryId: 20
        },
        {
          image: "/static/img/category/dahai.jpeg",
          tag: "大海",
          description: "蔚蓝无垠，聆听海浪轻拍心灵的声音。",
          categoryId: 19
        },
        {
          image: "/static/img/category/daoyu.jpeg",
          tag: "岛屿",
          description: "远离尘嚣，寻找属于自己的世外桃源。",
          categoryId: 29
        },
        {
          image: "/static/img/category/pubu.jpeg",
          tag: "瀑布",
          description: "飞流直下，定格自然的动感与力量。",
          categoryId: 27
        },
        {
          image: "/static/img/category/senlin.jpeg",
          tag: "森林",
          description: "绿意盎然，走进自然的呼吸与静谧。",
          categoryId: 23
        },
        {
          image: "/static/img/category/shamo.jpeg",
          tag: "沙漠",
          description: "浩瀚黄沙，感受孤独与自由的碰撞。",
          categoryId: 24
        },
        {
          image: "/static/img/category/shanfeng.jpeg",
          tag: "山峰",
          description: "登高望远，感受巍峨山峦的壮丽之美。",
          categoryId: 18
        },
        {
          image: "/static/img/category/tiankong.jpeg",
          tag: "天空",
          description: "仰望天空，捕捉云卷云舒的浪漫时刻。",
          categoryId: 21
        },
        {
          image: "/static/img/category/xiagu.jpeg",
          tag: "峡谷",
          description: "纵深幽远，探索大地裂痕的神秘壮观。",
          categoryId: 28
        },
        {
          image: "/static/img/category/xingkong.jpeg",
          tag: "星空",
          description: "星河灿烂，记录夜晚最浪漫的瞬间。",
          categoryId: 32
        },
        {
          image: "/static/img/category/qita.jpeg",
          tag: "其他",
          description: "未被定义的美，也值得用心去发现。",
          categoryId: 31
        }
      ]
    };
  },
  methods: {
    goToCategory(categoryId) {
      window.location.href = `#/Share?classId=${categoryId}`;
    }
  }
};
</script>


<style scoped>
.featured-posts {
  padding: 40px;
  font-family: Arial, sans-serif;
  max-width: 1200px;       /* ✅ 控制内容最大宽度 */
  margin: 0 auto;           /* ✅ 页面居中 */
  background-color: #fff;   /* ✅ 与灰色背景形成对比，有“模块”感 */
  border-radius: 12px;      /* ✅ 模块边角圆润 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); /* ✅ 模块阴影 */
}

.title {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 24px;
}

.card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: center; /* ✅ 居中排列卡片 */
}

.card {
  width: calc(33.333% - 16px);
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease;
}

.card:hover {
  transform: translateY(-5px);
}

.card-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
}

.card-content {
  padding: 12px;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.tag {
  background-color: #000;
  color: #fff;
  font-size: 16px;
  padding: 4px 10px;
  border-radius: 999px;
}

.card-description {
  font-size: 16px;
  color: #555;
  font-weight: bold;
  line-height: 1.4;
  height: 30px;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 1024px) {
  .card {
    width: calc(50% - 12px);
  }
}

@media (max-width: 640px) {
  .card {
    width: 100%;
  }
}
</style>
