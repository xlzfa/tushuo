<!-- 赞赏页面 -->
<template>
<div>
	<sg-nav></sg-nav>
	<div class="container" style="margin-top: 60px; max-width: 2000px; margin-left: auto; margin-right: auto;">
		
    <sg-reward></sg-reward>

	</div>
</div>
</template>

<script>
import header from '../components/header.vue'
import rightlist from '../components/rightlist.vue'
import reward from '../components/reward.vue'
export default {
	name: 'Reward',
	data() { //选项 / 数据
		return {

		}
	},
	methods: { //事件处理器

	},
	components: { //定义组件
		'sg-nav': header,
		'sg-reward': reward,
		'sg-rightlist': rightlist,
	},
	created() { //生命周期函数

	}
}
</script>

<style>

</style>
