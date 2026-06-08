<template>
  <div class="header">
    <div class="system-title">
      <span style="color:#20B486">K12信息技术编程在线学习平台</span>
    </div>
    <div class="menu-list">
      <div @click="toPage('index')" :class="$route.path == '/'?'menu_item':''">首页</div>
      <div @click="toPage('task')" :class="$route.path == '/task'?'menu_item':''">课程</div>
      <div @click="toPage('teacher')" :class="$route.path == '/teacher'?'menu_item':''">教师</div>
      <!--            <div @click="toPage('article')" :class="$route.path == '/article'?'menu_item':''">笔记</div>-->
      <div @click="toPage('forum')" :class="$route.path == '/forum'?'menu_item':''">论坛</div>
      <div @click="toPage('notice')" :class="$route.path == '/notice'?'menu_item':''">公告</div>
      <div @click="toPage('message')" :class="$route.path == '/message'?'menu_item':''">留言</div>
    </div>
    <div class="center">
      <el-dropdown @command="handleCommand">
                <span class="el-dropdown-link">
                    <img style="border-radius:50%;width:45px;height:45px" :src="$store.state.HOST + userInfo.avatar">
                </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="a"><div class="drop-item"><i class="el-icon-user"></i>个人中心</div></el-dropdown-item>
          <el-dropdown-item command="b"><div class="drop-item"><i class="el-icon-notebook-2"></i>我的课程</div></el-dropdown-item>
          <el-dropdown-item command="i"><div class="drop-item"><i class="el-icon-collection"></i>收藏课程</div></el-dropdown-item>
<!--          <el-dropdown-item command="c"><div class="drop-item"><i class="el-icon-edit"></i>记笔记</div></el-dropdown-item>-->
          <el-dropdown-item command="d"><div class="drop-item"><i class="el-icon-reading"></i>我的作业</div></el-dropdown-item>
          <el-dropdown-item command="j"><div class="drop-item"><i class="el-icon-document"></i>我的考试</div></el-dropdown-item>
          <el-dropdown-item command="e"><div class="drop-item"><i class="el-icon-notebook-1"></i>我的笔记</div></el-dropdown-item>
          <el-dropdown-item command="f"><div class="drop-item"><i class="el-icon-chat-dot-round"></i>我的帖子</div></el-dropdown-item>
<!--          <el-dropdown-item command="g"><div class="drop-item"><i class="el-icon-circle-close"></i>错题集</div></el-dropdown-item>-->
          <el-dropdown-item command="h" divided><div class="drop-item"><i class="el-icon-back"></i>退出登陆</div></el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <span class="username">{{userInfo.userName}}</span>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return{
      userInfo: {},
    }
  },
  methods: {
    handleCommand(command) {
      if (command == 'a') {
        this.$router.push("/center")
      } else if(command == 'b') {
        this.$router.push("/myTask")
      } else if(command == 'c') {
        this.$router.push("/addArticle")
      } else if(command == 'd') {
        this.$router.push("/myAssign")
      } else if(command == 'j') {
        this.$router.push("/test")
      } else if(command == 'e') {
        this.$router.push("/myArticle")
      } else if(command == 'f') {
        // 我的帖子 - 跳转到我的帖子页面
        this.$router.push("/myForum")
      } else if(command == 'g') {
        this.$router.push("/myWrongAssign")
      } else if(command == 'i') {
        this.$router.push("/myFavor")
      } else if(command == 'h') {
        this.$store.dispatch('logout').then(() => {
          window.localStorage.removeItem("user_info")
          window.localStorage.removeItem("user_token")
          this.$message({
            message: '退出成功',
            type: 'success'
          });
          setTimeout(function(){
            window.location.reload()
          },1000)
        })
      }
    },
    toPage(name) {
      if(name == 'about') {
        this.$router.push("/about")
      } else if (name == 'index') {
        this.$router.push("/")
      } else if (name == "task") {
        this.$router.push("/task")
      } else if (name == "teacher") {
        this.$router.push("/teacher")
      } else if (name == "message") {
        this.$router.push("/message")
      } else if (name == "article") {
        this.$router.push("/article")
      } else if (name == "notice") {
        this.$router.push("/notice")
      } else if (name == "forum") {
        this.$router.push("/forum")
      }
    },
  },
  created() {

  },
  mounted() {
    this.userInfo = JSON.parse(window.localStorage.getItem("user_info"))
  }
}
</script>

<style scoped>
@import url("../../assets/css/index/header.css");
</style>