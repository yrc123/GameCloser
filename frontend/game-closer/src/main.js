import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import axios from 'axios'

Vue.config.productionTip = false
Vue.use(ElementUI);

import * as api from '../api/api.js'
Vue.prototype.$api = api.default

new Vue({
  router,
	el: '#app',
  render: h => h(App)
}).$mount('#app')
