<template>
  <div>
    <sg-nav></sg-nav>
    <div class="container" style="margin-top: 60px; max-width: 1000px; margin-left: auto; margin-right: auto;">
      <el-row :gutter="30">
        <el-col :sm="24" :md="16" style="transition:all .5s ease-out;margin-bottom:30px;">
          <!-- 传递 searchText 和分页参数 -->
          <sg-articlelist :searchText="searchText" :pageNum="pageNum" :pageSize="pageSize"></sg-articlelist>
        </el-col>
        <el-col :sm="24" :md="8">
          <sg-rightlist></sg-rightlist>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import header from '../components/header.vue'
import articlelist from '../components/articlelist.vue'
import rightlist from '../components/rightlist.vue'

export default {
  name: "Search",
  data() {
    return {
      searchText: this.$route.query.title || '',  // 获取搜索参数
      pageNum: parseInt(this.$route.query.pageNum) || 1,
      pageSize: parseInt(this.$route.query.pageSize) || 10
    };
  },
  components: {
    'sg-nav': header,
    'sg-articlelist': articlelist,
    'sg-rightlist': rightlist
  }
}
</script>
