<!-- 头部公用 -->
<template>
<div class="">
	<div class="headBack" style="height: 60px">
		<div class="logo">
                    <img src="static/img/tushuologo.png" alt="Logo" class="logo-img">
                </div>
		<el-row class="container">
			
			<el-col :span="24">
				<!-- pc端导航 -->
				
				<div class="headBox">
					<el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect" :router="true">
						<div class="menu-container">
							<el-menu-item index="/Reward"><i class="fa fa-wa fa-tags"></i>分类</el-menu-item>
							<el-menu-item index="/Home"><i class="fa fa-wa fa-comments-o"></i>博客</el-menu-item>
							<el-menu-item index="/Map"><i class="fa fa-wa fa-road"></i>计划</el-menu-item>
							<!-- <el-submenu index="/Share">
								
								<el-menu-item v-for="(item,index) in classListObj" :key="'class1'+index" :index="'/Share?classId='+item.id">{{item.name}}</el-menu-item>
							</el-submenu> -->
							<!-- <el-menu-item index="/Reward"><i class="fa fa-wa fa-cny"></i>赏</el-menu-item> -->
							
						</div>

						<div class="userInfo">
							<div v-show="!haslogin" class="nologin">
								<a href="javascript:void(0);" @click="logoinFun(1)"><i class="fa user"></i>登录</a>
							</div>
							<div v-show="haslogin" class="haslogin">
                                <img  :src="userInfoObj.avatar?userInfoObj.avatar:'static/img/tou.jpg'"   :onerror="$store.state.errorImg" class="avatar">
								<span class="nickname">{{userInfoObj.nickName?userInfoObj.nickName:"无"}}</span>
								<!-- <i class="fa fa-fw fa-user-circle userImg"></i> -->
								
									<i class="fa fa-wa fa-pencil-square-o" aria-hidden="true" @click="write"></i>
								
								<ul class="haslogin-info">
									<li>
										<a href="#/UserInfo">个人中心</a>
									</li>
									<li>
										<a href="javascript:void(0);" @click="userlogout">退出登录</a>
									</li>
								</ul>
							</div>
						</div>
					</el-menu>
				</div>
			</el-col>
		</el-row>
	</div>
	<!-- <div class="headImgBox" :style="{backgroundImage:this.$store.state.themeObj.top_image?'url('+this.$store.state.themeObj.top_image+')':'url(static/img/headbg05.jpg)'}">
		<div class="scene">
			<div><span id="luke"></span></div>
		</div>
		<div class="h-information">

                    <img :src="this.$store.state.themeObj.head_portrait?this.$store.state.themeObj.head_portrait:'static/img/tou.png'" alt="">

			<h2 class="h-description">

                        {{this.$store.state.themeObj.autograph?this.$store.state.themeObj.autograph:"博客"}}

                </h2>
		</div> -->
</div>
</template>
<script>
import {logout} from '../api/user'
import {removeToken} from '../utils/auth'
import {getCategoryList} from '../api/category'
import {getUserInfo,savaUserInfo} from '../api/user.js'//获取用户信息，保存用户信息
import {
	Typeit
} from '../utils/plug.js'
export default {
	data() { //选项 / 数据
		return {
			userInfo: '', //用户信息
			userInfoObj:'',
			haslogin: false, //是否已登录
			classListObj: '', //分类
			activeIndex: '/', //当前选择的路由模块
			state: '', //icon点击状态
			pMenu: true, //手机端菜单打开
			// path:'',//当前打开页面的路径
			input: '', //input输入内容
			headBg: 'url(static/img/headbg05.jpg)', //头部背景图
			headTou: '', //头像
			projectList: '' //项目列表
		}
	},
	methods: { //事件处理器
		handleOpen(key, keyPath) { //分组菜单打开
			// console.log(key, keyPath);
		},
		handleClose(key, keyPath) { //分组菜单关闭
			// console.log(key, keyPath);
		},
		searchChangeFun(e) { //input change 事件
			// console.log(e)
			if (this.input == '') {
				this.$store.state.keywords = '';
				this.$router.push({path:'/'});
			}
		},
		getCategoryList(){
			getCategoryList().then((response)=>{
				this.classListObj = response
			})
		},
		handleSelect(key, keyPath) { //pc菜单选择
			//    console.log(key, keyPath);
		},
		write() {
      		this.$router.push('/write')
    	},
		logoinFun: function(msg) { //用户登录和注册跳转
			// console.log(msg);
			localStorage.setItem('logUrl', this.$route.fullPath);
			// console.log(666,this.$router.currentRoute.fullPath);
			if (msg == 0) {
				this.$router.push({
					path: '/Login?login=0'
				});
			} else {
				this.$router.push({
					path: '/Login?login=1'
				});
			}
		},
		// 用户退出登录
		userlogout: function() {
			var that = this;
			this.$confirm('是否确认退出?', '退出提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				// console.log(that.$route.path);

				logout().then((response)=>{
					removeToken()
					localStorage.removeItem('userInfo');
					that.haslogin = false;
					window.location.reload();
						that.$message({
							type: 'success',
							message: '退出成功!'
						});
					if (that.$route.path == '/UserInfo') {
						that.$router.push({
							path: '/'
						});
					}
				})
			}).catch(() => {
				//
			});

		},
		routeChange: function() {
			var that = this;
			that.pMenu = true
			this.activeIndex = this.$route.path == '/' ? '/Home' : this.$route.path;
			if (localStorage.getItem('userInfo')) { //存储用户信息
				console.log("localStorage:" + localStorage.getItem('userInfo'));
				that.haslogin = true;
				that.userInfo = JSON.parse(localStorage.getItem('userInfo'));
				
				console.log("打印登录后的userinfo" + that.userInfo.nickName);
				
				getUserInfo(that.userId).then((response)=>{
					that.userInfoObj = response;
					that.userInfoObj.head_start = 0;
				})
			} else {
				that.haslogin = false;
			}
			
			//获取分类
			this.getCategoryList()
			
			if ((this.$route.name == "Share" || this.$route.name == "Home") && this.$store.state.keywords) {
				this.input = this.$store.state.keywords;
			} else {
				this.input = '';
				this.$store.state.keywords = '';
			}
			console.log("打印haslogin：" + that.haslogin);
		}
	},
	components: { //定义组件

	},
	watch: {
		// 如果路由有变化，会再次执行该方法
		'$route': 'routeChange'
	},
	created() { //生命周期函数
		//判断当前页面是否被隐藏
		var that = this;
		var hiddenProperty = 'hidden' in document ? 'hidden' :
			'webkitHidden' in document ? 'webkitHidden' :
			'mozHidden' in document ? 'mozHidden' :
			null;
		var visibilityChangeEvent = hiddenProperty.replace(/hidden/i, 'visibilitychange');
		var onVisibilityChange = function() {
			
			if (!document[hiddenProperty]) { //被隐藏
				if (that.$route.path != '/DetailShare') {
					if (localStorage.getItem('userInfo')) {
						that.haslogin = true;
					} else {
						that.haslogin = false;
					}
				}
			}
		}
		document.addEventListener(visibilityChangeEvent, onVisibilityChange);
		// console.log();
		this.routeChange();


	},
	mounted() { //页面元素加载完成
		var that = this;
		var timer = setTimeout(function() {
			Typeit(that.$store.state.themeObj.user_start, "#luke"); //打字机效果
			clearTimeout(timer);
		}, 500);
	}
}
</script>

<style>
/*********头部导航栏********/

/*头部导航栏盒子*/

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.headBack {
	width: 100%;
	background-color: #fff; 
	/*margin-bottom:30px;*/
	box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .12), 0 0 6px 0 rgba(0, 0, 0, .04);
	position: fixed;
	left: 0;
	top: 0;
	right: 0;
	z-index: 100;
}

.headBox li.is-active {
	/*background: #48456C;*/
	background: #fff
}

.menu-container {
	display: flex;
  	justify-content: center; /* 水平居中 */
  	align-items: center;     /* 垂直居中（如果需要） */
}

.el-menu--horizontal>.el-submenu.is-active .el-submenu__title {

	border-bottom: none!important;
}


.headBox .el-menu {
	background: transparent;
	border-bottom: none!important;
}

.headBox .el-menu-demo li.el-menu-item,
.headBox .el-menu--horizontal .el-submenu .el-submenu__title {
	height: 60px;
	line-height: 60px;
	border-bottom: none!important;

}

.headBox .el-submenu li.el-menu-item {
	height: 60px;
	line-height: 60px;
}

.headBox li .fa-wa {
	vertical-align: baseline;
}

.headBox ul li.el-menu-item,
.headBox ul li.el-menu-item.is-active,
.headBox ul li.el-menu-item:hover,
.headBox .el-submenu div.el-submenu__title,
.headBox .el-submenu__title i.el-submenu__icon-arrow {
	color: #000;
}

.headBox .el-menu--horizontal .el-submenu>.el-menu {
	top: 60px;
	border: none;
	padding: 0;
}

.headBox>ul li.el-menu-item:hover,
.headBox>ul li.el-submenu:hover .el-submenu__title {
	background: #f8f8f8; /* 微妙的背景变色，接近白色但稍浅 */
    border-bottom: none;
}

.headBox>ul .el-submenu .el-menu,
.headBox>ul .el-submenu .el-menu .el-menu-item {
	background: #f5f5f5;
}

.headBox>ul .el-submenu .el-menu .el-menu-item {
	min-width: 0;
}

.headBox>ul .el-submenu .el-menu .el-menu-item:hover {
	background: #f5f5f5;
}



/*pc搜索框*/

.headBox .pcsearchbox {
	padding: 0;
	max-width: 170px;
	/*min-width: 30px;*/
	height: 100%;
	line-height: 38px;
	position: absolute;
	top: 0;
	right: 0;
	cursor: pointer;
}

.headBox .pcsearchbox:hover .pcsearchinput {
	opacity: 1;
	/*transform: scaleX(1);*/
	visibility: visible;
}

.headBox .pcsearchbox i.pcsearchicon {
	color: #fff;
	padding-left: 10px;
}

.headBox .pcsearchbox .pcsearchinput {
	width: 180px;
	padding: 10px 20px 10px 20px;
	background: rgba(40, 42, 44, 0.6);
	border-radius: 0 0 2px 2px;
	position: absolute;
	right: 0;
	top: 38px;
	opacity: 0;
	visibility: hidden;
	/*transform: scaleX(0);*/
	transform-origin: right;
	transition: all 0.3s ease-out;
}

.headBox .pcsearchbox .hasSearched {
	opacity: 1;
	/*transform: scaleX(1);*/
	visibility: visible;
}

.headBox .pcsearchbox .el-input {
	width: 100%;
}

.headBox .el-input__inner {
	height: 30px;
	border: none;
	background: #fff;
	/*border: 1px solid #333;*/
	border-radius: 2px;
	padding-right: 10px;
}

.headBox .userInfo {
	height: 100%;
	line-height: 60px;
	position: absolute;
	right: 30px;
	top: 0;
	color: #000;
}

.headBox .userInfo a {
	color: #000;
	font-size: 13px;
	transition: all 0.2s ease-out;
}

.headBox .userInfo a:hover {
	color: #48456C;
}

.headBox .nologin {
	text-align: right;
}
/* 已登录 */
/* .headBox .haslogin {
	display: flex;
	justify-content: space-between;
  	align-items: center;
	background-color: #f6f6f6;
	text-align: right;
	position: relative;
	height: 60px;
	min-width: 170px;
	border-radius: 50px;
	border-color: #000;
} */

.headBox .haslogin:hover ul {
	visibility: visible;
	opacity: 1;
}

.headBox .haslogin ul {
    background: #f5f5f5;
    padding: 5px 10px;
    position: absolute;
    top: 100%; /* 定位在父容器的底部 */
    left: 0; /* 水平对齐到父容器的左边 */
    right: 0; /* 水平对齐到父容器的右边 */
    visibility: hidden;
    opacity: 0;
	text-align: center; /* 文字居中 */
    transition: all 0.3s ease-out;
}

.headBox .haslogin ul li {
	border-bottom: 1px solid #48456C;
}

.headBox .haslogin ul li:last-child {
	border-bottom: 1px solid transparent;
}

/*******移动端*******/

.mobileBox {
	position: relative;
	height: 38px;
	line-height: 38px;
	color: #fff;
}

.hideMenu {
	position: relative;
	width: 100%;
	height: 100%;
	line-height: 38px;
}

.hideMenu ul.mlistmenu {
	width: 100%;
	position: absolute;
	left: 0;
	top: 100%;
	box-sizing: border-box;
	z-index: 999;
	box-shadow: 0 2px 6px 0 rgba(0, 0, 0, .12), 0 0 8px 0 rgba(0, 0, 0, .04);
	background: #48456C;
	color: #fff;
	border-right: none;
}

.hideMenu .el-submenu .el-menu {
	background: #f5f5f5;
}

.hideMenu .el-menu-item,
.hideMenu .el-submenu__title {
	color: #fff;
}

.hideMenu>i {
	position: absolute;
	left: 10px;
	top: 12px;
	width: 30px;
	height: 30px;
	font-size: 16px;
	color: #fff;
	cursor: pointer;
}

.hideMenu .el-menu-item,
.el-submenu__title {
	height: 40px;
	line-height: 40px;
}

.mobileBox .searchBox {
	padding-left: 40px;
	width: 100%;
	box-sizing: border-box;
}

.mobileBox .searchBox .el-input__inner {
	display: block;
	border-radius: 2px;
	border: none;
	height: 25px;
}

.hideMenu ul.mlistmenu.pshow {
	display: block;
}

.hideMenu ul.mlistmenu .el-submenu__icon-arrow,
.mobileBox li.el-menu-item a {
	color: #fff;
}

.hideMenu>ul li.el-menu-item:hover,
.hideMenu>ul li.el-menu-item.is-active {
	background: #f6f6f6;
}



/*头部背景图*/

.headImgBox {
	height: 650px;
	position: relative;
	width: 100%;
	background-size: cover;
	background-position: center 50%;
	background-repeat: no-repeat;
	margin-bottom: 90px;
}

.h-information {
	text-align: center;
	width: 70%;
	margin: auto;
	position: relative;
	top: 480px;
	padding: 40px 0;
	font-size: 16px;
	opacity: 0.98;
	background: rgba(230, 244, 249, 0.8);
	border-radius: 5px;
	z-index: 1;
	animation: b 1s ease-out;
	-webkit-animation: b 1s ease-out;
}

@-webkit-keyframes b {
	0% {
		-webkit-transform: translateY(90px);
		transform: translateY(90px);
	}
	80% {
		-webkit-transform: translateY(5px);
		transform: translateY(5px)
	}
	90% {
		-webkit-transform: translateY(-5px);
		transform: translateY(-5px)
	}
	to {
		-webkit-transform: translateY(0);
		transform: translateY(0)
	}
}

@keyframes b {
	0% {
		-webkit-transform: translateY(90px);
		transform: translateY(90px);
	}
	80% {
		-webkit-transform: translateY(5px);
		transform: translateY(5px)
	}
	90% {
		-webkit-transform: translateY(-5px);
		transform: translateY(-5px)
	}
	to {
		-webkit-transform: translateY(0);
		transform: translateY(0)
	}
}

.h-information img {
	width: 100px;
	height: 100px;
	border-radius: 100%;
	transition: all .4s ease-in-out;
	-webkit-transition: all .4s ease-in-out;
	object-fit: cover;
}

.h-information img:hover {
	transform: rotate(360deg);
	-webkit-transform: rotate(360deg);
}

.h-information h2 {
	margin-top: 20px;
	font-size: 18px;
	font-weight: 700;
	/*font-family: 'Sigmar One';*/
}
.h-information h2  a{
	background: linear-gradient(to right, #DF2050, #48456D);
	-webkit-background-clip: text;
	color: transparent;
}
.headImgBox .scene {
	width: 100%;
	/*height:300px;*/
	text-align: center;
	font-size: 100px;
	font-weight: 200;
	color: #fff;
	position: absolute;
	left: 0;
	top: 160px;
	font-family: 'Sigmar One', Arial;
	text-shadow: 0 2px 2px #47456d;

}

.headImgBox .scene span {
	transform: matrix(1, 0, 0, 1, 0, 0);
	-webkit-transform: matrix(1, 0, 0, 1, 0, 0);
	text-shadow: 1px 1px 0 #ff3f1a, -1px -1px 0 #00a7e0;
}

.saying:after {
	content: "|";
	font-family: Arial, sans-serif;
	font-size: 1em;
	/*line-height: 0;*/
	display: inline-block;
	vertical-align: baseline;
	opacity: 1;
	text-shadow: 1px 1px 0 #ff3f1a, -1px -1px 0 #00a7e0;
	animation: caret 500ms infinite;
}
.avatar-uploader {
    display: inline-block;
    vertical-align: top;
}


.headBox .haslogin {
    display: flex;
    align-items: center; /* 垂直居中 */
    background-color: #f6f6f6;
    text-align: right;
    position: relative;
    height: 60px;
    min-width: 170px;
    border-radius: 50px;
    border-color: #000;
    padding: 0 10px; /* 添加一些内边距，避免内容紧贴边缘 */
}

.avatar {
    max-width: 50px;
    max-height: 50px;
    display: inline-block;
    vertical-align: middle;
    object-fit: cover;
    border-radius: 50%;
    border: 1px solid #fff;
}

.nickname {
    font-size: 18px;
    margin-left: 10px; /* 与 avatar 保持一定距离 */
}

.fa-pencil-square-o {
    font-size: 20px; /* 设置图标大小 */
    cursor: pointer; /* 鼠标悬停时显示手型 */
    margin-left: auto; /* 推到最右边 */
}




/* .nickname {
	position: absolute;
	margin-left: 5px;
	font-size: 18px;
}
.fa-pencil-square-o {
	position: absolute;
    font-size: 20px;
    cursor: pointer;
}
.avatar {
	max-width: 50px;
    max-height: 50px;
    display: inline-block;
    vertical-align: middle;
    object-fit: cover;
    border-radius: 50%;
    position: absolute;
    top: 50%;
    left: 0;
	margin-left: 5px;
	margin-right: 5px;
	border: 1px solid #fff;
    transform: translateY(-50%);
} */

.logo {
    position: absolute; /* 使用绝对定位 */
    left: 10px; /* 距离左侧10px */
    top: 50%; /* 垂直居中 */
    transform: translateY(-50%); /* 垂直居中 */
}

.logo-img {
    max-width: 100%; /* 使图片宽度最大为容器宽度 */
    max-height: 45px; /* 使图片高度最大为导航栏高度 */
    width: auto; /* 保持图片宽度自动 */
    height: auto; /* 保持图片高度自动 */
    object-fit: contain; /* 保持图片宽高比 */
}


@keyframes caret {
	0%,
	100% {
		opacity: 1;
	}
	50% {
		opacity: 0;
	}
}
</style>
