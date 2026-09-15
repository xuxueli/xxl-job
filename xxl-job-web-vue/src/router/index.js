import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/login",
    name: "Login",
    component: function () {
      return import("@/views/login/LoginPage.vue");
    },
    meta: { noAuth: true, title: "登录" },
  },
  {
    path: "/",
    redirect: "/jobinfo",
  },
  {
    path: "/jobinfo",
    name: "JobInfo",
    component: function () {
      return import("@/views/jobinfo/JobInfoIndex.vue");
    },
    meta: { title: "任务管理" },
  },
  {
    path: "/jobquery",
    name: "JobQuery",
    component: function () {
      return import("@/views/jobinfo/JobQueryList.vue");
    },
    meta: { title: "定时任务查询" },
  },
  {
    path: "/joblog",
    name: "JobLog",
    component: function () {
      return import("@/views/joblog/JobLogList.vue");
    },
    meta: { title: "定时任务执行记录查询" },
  },
  {
    path: "/jobgroup",
    name: "JobGroup",
    component: function () {
      return import("@/views/jobgroup/JobGroupIndex.vue");
    },
    meta: { title: "执行器管理" },
  },
  {
    path: "/user",
    name: "User",
    component: function () {
      return import("@/views/user/UserIndex.vue");
    },
    meta: { title: "用户管理" },
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: function () {
      return import("@/views/dashboard/DashboardIndex.vue");
    },
    meta: { title: "运行报表" },
  },
];

const router = new VueRouter({
  routes,
});

router.beforeEach(function (to, from, next) {
  var isLoggedIn = sessionStorage.getItem("xxl-job-login") === "true";

  if (to.meta.noAuth) {
    // 已登录用户访问登录页，重定向到首页
    if (isLoggedIn) {
      next("/");
    } else {
      next();
    }
    return;
  }

  if (!isLoggedIn) {
    next({ path: "/login", query: { redirect: to.fullPath } });
  } else {
    next();
  }
});

export default router;
