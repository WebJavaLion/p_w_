import VueRouter from "vue-router";
import Vue from "vue";
import WordGroupList from "./components/WordGroupList.vue";
import LoginForm from "./pages/LoginForm.vue";
Vue.use(VueRouter);

const routes = [
    {path: '/', component: WordGroupList},
    {path: '/api/log', component: LoginForm},
    {path: '*', component: WordGroupList}
];

export default new VueRouter({
    mode: 'history',
    routes: routes
})