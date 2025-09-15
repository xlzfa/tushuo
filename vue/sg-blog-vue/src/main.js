// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
// import 'element-ui/lib/theme-chalk/index.css';
import './assets/css/style.less'
import store from './store'
import plugins from './plugins' // plugins
import MavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

import BaiduMap from 'vue-baidu-map'

Vue.use(BaiduMap, {
  // ak 是在百度地图开发者平台申请的密钥 详见 http://lbsyun.baidu.com/apiconsole/key */
  ak: 'aRXvRzFwqrV1hWsACRVEOjOarq12iBxD'
})

// import AvueMap from 'avue-plugin-map';  // 导入地图插件

// // 配置地图插件
// Vue.use(AvueMap, {
//   amapKey: '9b5519c09042483cb364810b465626a4',  // 替换成你的高德地图 API KEY
// });

// import VueAMap from 'vue-amap'

// Vue.use(VueAMap)

// VueAMap.initAMapApiLoader({
//   key: '9b5519c09042483cb364810b465626a4',
//   plugin: ['AMap.Autocomplete', 'AMap.PlaceSearch', 'AMap.Geocoder', 'AMap.Marker'],
//   v: '1.4.15'
// })

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.use(MavonEditor)
Vue.use(plugins)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>',
  store
})
