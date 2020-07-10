import Vue from 'vue'
import App from 'pages/App.vue'
import VueResource from 'vue-resource'
import '@babel/polyfill'
import store from './store'
import Vuetify from 'vuetify';
import 'vuetify/dist/vuetify.min.css'
import router from "./router";

Vue.use(VueResource);
Vue.use(Vuetify);

new Vue({
   router: router,
   vuetify : new Vuetify({
      icons: {iconfont: 'mdiSvg'},
      defaultAssets: {
         font: true,
         icons: 'md'
      },
   }),
   store: store,
   el: '#app',
   render: a => a(App)
});