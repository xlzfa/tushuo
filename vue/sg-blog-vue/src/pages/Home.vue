<!-- 首页 -->
<template>
    <div style="background-color: #f6f6f6;">
        <sg-navbar></sg-navbar>
        <div class="container" style="margin-top: 60px; max-width: 1000px; margin-left: auto; margin-right: auto;">
            <el-row :gutter="10">
                <el-col :sm="24" :md="18" style="transition:all .5s ease-out; margin-bottom:30px;">
                    <sg-articlelist></sg-articlelist>
                </el-col>
                <el-col :sm="24" :md="6">
                    <sg-rightlist></sg-rightlist>
                </el-col>
            </el-row>
        </div>
        <sg-footer></sg-footer>
    </div>
</template>

<script>
import header from '../components/header.vue'
import articlelist from '../components/articlelist.vue'
import rightlist from '../components/rightlist.vue'
import footer from "../components/footer.vue";
    export default {
        name:'Home',
        data() { //选项 / 数据
            return {

            }
        },
        methods: { //事件处理器

        },
        components: { //定义组件
            'sg-footer':footer,
            'sg-navbar':header,
            'sg-articlelist':articlelist,
            'sg-rightlist':rightlist,
        },
        created() { //生命周期函数

        }
    }
</script>
