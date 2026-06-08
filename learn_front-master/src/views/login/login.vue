<template>
  <div class="login">
	<div class="login-top">
		<div class="logo"></div>
		<div class="btns">
			<div style="padding-right:100px;display:flex">
				<div class="toReg" @click="toRegister">
					<div>注册</div>
				</div>
				<div class="toRegTeacher" @click="toTeacherRegister">
					<div>教师入驻</div>
				</div>
			</div>
		</div>
	</div>
	<div class="login-center">
		<div class="login-content">
			<div style="padding-left: 100px;">
				<div class="title">
					<div>STUDY ONLINE </div>
					<div>WITH EXPERT</div>
				</div>
				<div style="color:#181818;font-family:'黑体'">K12编程新乐园，师生线上共筑编程梦</div>
				<div style="margin-top:20px;color:#181818;font-family:'黑体';text-indent:2em;">欢迎来到 K12 信息技术编程的精彩世界！我们的在线平台是师生互动的活力空间。老师们能在此轻松授课，分享丰富编程资源，为学生的编程学习引路导航。学生们既可以在课堂上跟随老师的讲解探索代码奥秘，也能在课后自主搜索资源，开启自学之旅。独自钻研时，如同在编程宇宙中独自探险，发现无限可能；与同学交流学习时，就像组建编程小战队，思维碰撞间激发创新火花，一起在编程的天地里快乐成长！</div>
				<div style="margin-top:30px;display:flex;justify-content: space-between">
					<img style="width:20%" src="../../assets/image/login_image.png">
					<img style="width:25%;padding-right:30px" src="../../assets/image/login-jiantou.png">
				</div>
			</div>
		</div>
		<div class="login-form">
			<div class="jiantou1">
				<img src="../../assets/image/jiantou1.png">
			</div>
			<div class="jiantou2">
				<img src="../../assets/image/jiantou2.png">
			</div>
			<div class="yuan">
				<img src="../../assets/image/yuan.png">
			</div>
			<div class="form">
				<div class="login-title">K12信息技术编程在线学习平台</div>
				<div style="width:100%">
					<el-form style="width:100%" :model="userInfo" :rules="rules" ref="ruleForm" class="demo-ruleForm">
						<el-form-item  prop="username">
							<el-input v-model="userInfo.username" placeholder="请输入用户账号"></el-input>
						</el-form-item>
						<el-form-item  prop="password">
							<el-input type="password" v-model="userInfo.password" placeholder="请输入用户密码"></el-input>
						</el-form-item>
					</el-form>
				</div>
        <div class="forget-password" @click="toForgetPassword">忘记密码？</div>
				<div class="login-btn" @click="toLogin()">
					<div>登 陆</div>
				</div>
			</div>
		</div>
	</div>
	<bottomPage></bottomPage>
  </div>
</template>

<script>
  import {login,getUser} from '../../api/api' 
  import bottomPage from "../../components/bottom/login-bottom"
  export default {
	data() {
	  return{
		userInfo: {
			username: "",
			password: ""
		},
		rules: {
          username: [
            { required: true, message: '请输入用户账号', trigger: 'blur' },
          ],
          password: [
            { required: true, message: '请输入用户密码', trigger: 'blur' }
          ],
        }
	  }
	},
	components: {
        bottomPage
	},
	methods: {
		toRegister() {
			this.$router.push("/register")
		},
		toTeacherRegister() {
			this.$router.push("/teacherRegister")
		},
		toLogin() {
			this.$refs["ruleForm"].validate((valid) => {
				if (valid) {
					var params = {
						username: this.userInfo.username,
						password: this.userInfo.password
					}
					login(params).then(res => {
						if(res.code == 1000) {
							this.$message({
								message: '登陆成功',
								type: 'success'
							});
							var that = this
							var token = res.data.token
							window.localStorage.setItem("user_token",token)
							this.getUserInfo()
							setTimeout(function() {
								that.$router.push("/")
							},500)
						} else {
							this.$message.error(res.message);
						}
					})
				} else {
					return false;
				}
			});
		},
    toForgetPassword() {
      console.log('=== 开始跳转 ===')
      console.log('当前路由:', this.$router.currentRoute.path)

      // 检查路由是否存在
      const routeExists = this.$router.options.routes.find(r => r.path === '/forgetPassword')
      console.log('路由是否存在:', routeExists)

      // 尝试跳转
      this.$router.push('/forgetPassword', () => {
        console.log('跳转成功回调')
      }, (error) => {
        console.log('跳转失败回调:', error)
      })

      // 跳转后检查
      setTimeout(() => {
        console.log('跳转后路由:', this.$router.currentRoute.path)
      }, 100)
    },
		getUserInfo() {
            getUser().then(res => {
                if(res.code == 1000) {
					window.localStorage.setItem("user_info",JSON.stringify(res.data))
                }
            })
        },
	},
	created() {
	 
	},
	mounted() {
	  
	}
 }
</script>

<style scoped>
  .login {
	  width: 100%;
	  height: 100%;
	  background-color: #FCFCFC;
  }
  .login-top {
	  width: 100%;
	  height: 100px;
	  display: flex;
	  flex-direction: row;
	  align-items: center;
	  justify-content: space-between;
  }
  .logo {
	  width: 30%;
	  text-align: center;
	  height: 100%;
	  display: flex;
	  align-items: center;
  }
  .btns {
	  width: 45%;
	  display: flex;
	  background-color: #ffffff;
	  height: 100%;
	  display: flex;
	  align-items: center;
	  justify-content: flex-end;
  }
  .toReg {
	  width: 130px;
	  height: 40px;
	  border:1px solid #474747;
	  font-family: '黑体';
	  cursor: pointer;
	  display: flex;
	  justify-content: center;
	  align-items: center;
  }
  .toRegTeacher {
	  margin-left: 20px;
	  width: 130px;
	  height: 40px;
	  border:1px solid #474747;
	  font-family: '黑体';
	  cursor: pointer;
	  display: flex;
	  justify-content: center;
	  align-items: center;
  }
  .login-center {
	  background-image: url('../../assets/image/index/index_back.png');
	  width: 100%;
	  height: 75%;
	  display: flex;
	  flex-direction: row;
  }
  .login-bottom {
	  width: 100%;
	  height: 250px;
	  background-color: #262543;
	  display: flex;
  }
  .login-content {
	  width: 55%;
	  height: 100%;
	  display: flex;
	  flex-direction: column;
  }
  .login-form {
	  background-image: url('../../assets/image/index/index_back.png');
	  position: relative;
	  display: flex;
	  justify-content: center;
	  width: 45%;
	  height: 100%;
	  background-color: #ffffff;
  }
  .title {
	  font-weight: '黑体';
	  font-size: 55px;
	  font-weight: bold;
  }
  .jiantou1 {
	  position: absolute;
	  left: 10px;
	  top: 20px;
  }
  .jiantou2 {
	  position: absolute;
	  left: 10px;
	  bottom: 100px;
  }
  .yuan {
	  position: absolute;
	  right: 10px;
	  bottom: 200px;
  }
  .form {
	  width: 50%;
	  height: 70%;
	  display: flex;
	  flex-direction: column;
	  align-items: center;
	  justify-content: space-around;
  }
  .login-title {
	  font-size: 40px;
	  font-weight: bold;
	  height: 40%;
	  display: flex;
	  align-items: center;
    white-space: nowrap;
  }
  .login-btn {
	  width: 130px;
	  height: 40px;
	  border:1px solid #FF5202;
	  background-color: #FF5202;
	  color: #ffffff;
	  font-size: 18px;
	  font-family: '黑体';
	  cursor: pointer;
	  display: flex;
	  justify-content: center;
	  align-items: center;
  }
  .forget-password {
    margin-top: 0px;
    color: #999;
    font-size: 14px;
    cursor: pointer;
    text-align: right;
    width: 100%;
    padding-right: 10px;
  }
  .forget-password:hover {
    color: #FF5202;
  }
</style>