<!-- 友情链接 -->
<template>
    <div>
        <sg-nav></sg-nav>
        <div class="container" style="margin-top: 60px; max-width: 2000px; margin-left: auto; margin-right: auto;">
            <sg-map></sg-map>
        </div>
    </div>
</template>

<script>
import header from '../components/header.vue'
import map from '../components/map.vue'
    export default {
        name: 'FriendsLink',
        data() { //选项 / 数据
            return {

            }
        },
        methods: { //事件处理器

        },
        components: { //定义组件
            'sg-nav':header,
            'sg-map':map,
        },
        created() { //生命周期函数

        }
    }
</script>

<style>

</style>
