<template>
  <div class="login">
    <div class="login-top">
      <div class="logo"></div>
      <div class="btns">
        <div style="padding-right:100px;display:flex">
          <div class="toReg" @click="toLogin">
            <div>登陆</div>
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
          <div class="login-title"></div>
          <div style="width:100%">
            <el-form style="width:100%" :model="userInfo" :rules="rules" ref="ruleForm" class="demo-ruleForm">
              <el-form-item prop="userName">
                <el-input v-model="userInfo.userName" placeholder="请输入姓名"></el-input>
              </el-form-item>
              <el-form-item prop="loginAccount">
                <el-input v-model="userInfo.loginAccount" placeholder="请输入用户账号"></el-input>
              </el-form-item>
              <el-form-item prop="tel">
                <el-input v-model="userInfo.tel" placeholder="请输入手机号"></el-input>
              </el-form-item>

              <!-- 生日选择器（字段名改为 birth） -->
              <el-form-item prop="birth">
                <el-date-picker
                    v-model="userInfo.birth"
                    type="date"
                    placeholder="请选择出生日期"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    style="width: 100%"
                    @change="calculateAge">
                </el-date-picker>
              </el-form-item>

              <!-- 年龄显示 -->
              <el-form-item prop="age">
                <el-input
                    v-model="userInfo.age"
                    placeholder="年龄（根据生日自动计算）"
                    readonly
                    disabled>
                  <template slot="prepend">年龄</template>
                </el-input>
              </el-form-item>

              <el-form-item prop="verifyCode">
                <div class="verify-code-input">
                  <el-input v-model="userInfo.verifyCode" placeholder="请输入验证码"></el-input>
                  <el-button
                      :disabled="countdown > 0"
                      @click="sendVerifyCode"
                      class="send-code-btn">
                    {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
                  </el-button>
                </div>
              </el-form-item>
              <el-form-item prop="password">
                <el-input type="password" v-model="userInfo.password" placeholder="请输入用户密码"></el-input>
              </el-form-item>
              <el-form-item prop="passwordNew">
                <el-input type="password" v-model="userInfo.passwordNew" placeholder="请输入确认密码"></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="login-btn" @click="toRegister()">
            <div>注 册</div>
          </div>
        </div>
      </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import { register } from '../../api/api'
import bottomPage from "../../components/bottom/login-bottom"
export default {
  data() {
    // 自定义验证规则：手机号格式
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的11位手机号'))
      } else {
        callback()
      }
    }

    // 自定义验证规则：验证码
    const validateVerifyCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入验证码'))
      } else if (!/^\d{6}$/.test(value)) {
        callback(new Error('验证码为6位数字'))
      } else {
        callback()
      }
    }

    // 自定义验证规则：生日
    const validateBirth = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择出生日期'))
      } else {
        callback()
      }
    }

    // 自定义验证规则：年龄
    const validateAge = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error('请先选择出生日期'))
      } else if (value < 0 || value > 120) {
        callback(new Error('年龄超出有效范围'))
      } else {
        callback()
      }
    }

    return{
      userInfo: {
        userName: "",
        password: "",
        passwordNew: "",
        loginAccount: "",
        tel: "",
        birth: "",        // 改为 birth（对应数据库字段）
        age: "",          // 年龄
        verifyCode: "",
        userType: 2
      },
      countdown: 0,
      rules: {
        userName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
        ],
        loginAccount: [
          { required: true, message: '请输入用户账号', trigger: 'blur' },
          { min: 4, max: 20, message: '账号长度在4-20个字符之间', trigger: 'blur' }
        ],
        tel: [
          { required: true, validator: validatePhone, trigger: 'blur' }
        ],
        birth: [          // 改为 birth
          { required: true, validator: validateBirth, trigger: 'change' }
        ],
        age: [
          { required: true, validator: validateAge, trigger: 'change' }
        ],
        verifyCode: [
          { required: true, validator: validateVerifyCode, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入用户密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
        ],
        passwordNew: [
          { required: true, message: '请输入确认密码', trigger: 'blur' }
        ]
      }
    }
  },
  components: {
    bottomPage
  },
  methods: {
    toLogin() {
      this.$router.push("/login")
    },
    toTeacherRegister() {
      this.$router.push("/teacherRegister")
    },

    /**
     * 根据生日计算年龄
     * @param {string} birth 生日日期 (yyyy-MM-dd)
     */
    calculateAge(birth) {
      if (!birth) {
        this.userInfo.age = ""
        return
      }

      const birthDate = new Date(birth)
      const today = new Date()

      let age = today.getFullYear() - birthDate.getFullYear()
      const monthDiff = today.getMonth() - birthDate.getMonth()

      // 如果还没到生日，年龄减1
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--
      }

      // 年龄验证
      if (age < 0) {
        this.$message.warning('出生日期不能晚于今天')
        this.userInfo.age = ""
        this.userInfo.birth = ""
        return
      }

      if (age > 120) {
        this.$message.warning('年龄超出合理范围')
        this.userInfo.age = ""
        this.userInfo.birth = ""
        return
      }

      this.userInfo.age = age

      // 手动触发表单验证
      this.$nextTick(() => {
        this.$refs.ruleForm.validateField('age')
        this.$refs.ruleForm.validateField('birth')
      })
    },

    // 发送验证码
    sendVerifyCode() {
      // 先验证手机号
      if (!this.userInfo.tel) {
        this.$message.error('请先输入手机号')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.userInfo.tel)) {
        this.$message.error('请输入正确的手机号格式')
        return
      }

      // 模拟发送验证码（测试用）
      this.$message.success('验证码已发送：123456')
      this.userInfo.verifyCode = '123456'

      // 开始倒计时
      this.countdown = 60
      const timer = setInterval(() => {
        if (this.countdown <= 1) {
          clearInterval(timer)
          this.countdown = 0
        } else {
          this.countdown--
        }
      }, 1000)
    },

    // 注册
    toRegister() {
      this.$refs["ruleForm"].validate(async (valid) => {
        if (valid) {
          // 验证密码一致性
          if (this.userInfo.password != this.userInfo.passwordNew) {
            this.$message({
              message: '两次输入的密码不一致！',
              type: 'warning'
            });
            return;
          }

          // 验证年龄是否已计算
          if (!this.userInfo.age && this.userInfo.age !== 0) {
            this.$message.error('请选择出生日期')
            return;
          }

          // 验证生日是否存在
          if (!this.userInfo.birth) {
            this.$message.error('请选择出生日期')
            return;
          }

          // 模拟验证码验证（测试用，验证码固定为123456）
          if (this.userInfo.verifyCode !== '123456') {
            this.$message.error('验证码错误（测试验证码为123456）')
            return;
          }

          // 准备提交数据 - 字段名与数据库对应
          const submitData = {
            userName: this.userInfo.userName,
            loginAccount: this.userInfo.loginAccount,
            tel: this.userInfo.tel,
            birth: this.userInfo.birth,    // 改为 birth
            age: this.userInfo.age,
            password: this.userInfo.password,
            userType: this.userInfo.userType
          }

          // 调试：打印提交的数据
          console.log('===== 注册提交数据 =====')
          console.log('userName:', submitData.userName)
          console.log('loginAccount:', submitData.loginAccount)
          console.log('tel:', submitData.tel)
          console.log('birth:', submitData.birth)    // 改为 birth
          console.log('age:', submitData.age)
          console.log('userType:', submitData.userType)
          console.log('========================')

          // 执行注册
          const registerRes = await register(submitData)
          if (registerRes.code == 1000) {
            this.$message({
              message: '注册成功',
              type: 'success'
            });
            var that = this
            setTimeout(function() {
              that.$router.push("/login")
            }, 500)
          } else {
            this.$message.error(registerRes.message);
          }
        } else {
          return false;
        }
      });
    }
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
  width: 55%;
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
  width: 100%;
  height: 75%;
  display: flex;
  flex-direction: row;
  background-image: url('../../assets/image/index/index_back.png');
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
  height: 85%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-around;
}
.login-title {
  font-size: 40px;
  font-weight: bold;
  height: 20%;
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
/* 验证码输入框样式 */
.verify-code-input {
  display: flex;
  gap: 10px;
  width: 100%;
}
.verify-code-input .el-input {
  flex: 1;
}
.send-code-btn {
  width: 110px;
  white-space: nowrap;
  background-color: #f0f0f0;
  border: 1px solid #dcdfe6;
  color: #606266;
}
.send-code-btn:hover:not(:disabled) {
  background-color: #FF5202;
  border-color: #FF5202;
  color: white;
}
.send-code-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}
/* 表单样式调整 */
.el-form-item {
  margin-bottom: 18px;
}
.bottom-one {
  width: 34%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.bottom-two {
  width: 22%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.bottom-three {
  width: 22%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.bottom-four {
  width: 22%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.one-1 {
  font-size: 40px;
  font-weight: bold;
  color: #ffffff;
}
.one-2 {
  margin-top: 15px;
  font-size: 18px;
  color: #ffffff;
  font-family: '黑体';
}
.two-2 {
  margin-top: 15px;
  font-size: 25px;
  color: #ffffff;
  font-weight: bold;
}
.two-3 {
  margin-top: 15px;
  font-size: 18px;
  color: #ffffff;
  font-family: '黑体';
}
.vertify {
  display: flex;
  align-items: center;
}

/* 年龄输入框样式 */
.el-input.is-disabled .el-input__inner {
  background-color: #f5f7fa;
  color: #606266;
}

/* 日期选择器样式 */
/deep/ .el-date-editor .el-input__inner {
  padding-left: 35px;
}
</style>