<template>
  <div class="forget-password-container">
    <div class="forget-password-box">
      <div class="back-link" @click="goBack">
        ← 返回登录
      </div>
      <h2 class="title">找回密码</h2>

      <!-- 步骤1：验证手机号 -->
      <div v-if="step === 1" class="step-form">
        <div class="form-item">
          <input type="text" v-model="formData.username" placeholder="请输入用户账号" class="input-field">
        </div>
        <div class="form-item">
          <input type="tel" v-model="formData.phone" placeholder="请输入绑定手机号" class="input-field">
        </div>
        <div class="form-item code-item">
          <input type="text" v-model="formData.verifyCode" placeholder="验证码" class="input-field code-input">
          <button class="send-code-btn" :disabled="countdown > 0" @click="sendCode">
            {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
          </button>
        </div>
        <button class="next-btn" @click="verifyPhone">下一步</button>
      </div>

      <!-- 步骤2：重置密码 -->
      <div v-if="step === 2" class="step-form">
        <div class="form-item">
          <input type="password" v-model="formData.newPassword" placeholder="请输入新密码" class="input-field">
        </div>
        <div class="form-item">
          <input type="password" v-model="formData.confirmPassword" placeholder="确认新密码" class="input-field">
        </div>
        <button class="next-btn" @click="resetPassword">确认修改</button>
      </div>

      <!-- 步骤3：完成 -->
      <div v-if="step === 3" class="success-box">
        <div class="success-icon">✓</div>
        <p class="success-text">密码重置成功！</p>
        <button class="login-btn" @click="goToLogin">立即登录</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'forgetPassword',
  data() {
    return {
      step: 1,
      formData: {
        username: '',
        phone: '',
        verifyCode: '',
        newPassword: '',
        confirmPassword: ''
      },
      countdown: 0
    }
  },
  methods: {
    goBack() {
      this.$router.push('/login')
    },
    goToLogin() {
      this.$router.push('/login')
    },
    sendCode() {
      if (!this.formData.phone) {
        alert('请输入手机号')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.formData.phone)) {
        alert('请输入正确的手机号')
        return
      }
      // 模拟发送验证码
      alert('验证码已发送：123456（测试用）')
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
    verifyPhone() {
      if (!this.formData.username) {
        alert('请输入用户账号')
        return
      }
      if (!this.formData.phone) {
        alert('请输入手机号')
        return
      }
      if (!this.formData.verifyCode) {
        alert('请输入验证码')
        return
      }
      // 模拟验证成功
      this.step = 2
    },
    resetPassword() {
      if (!this.formData.newPassword) {
        alert('请输入新密码')
        return
      }
      if (this.formData.newPassword !== this.formData.confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }
      if (this.formData.newPassword.length < 6) {
        alert('密码长度不能少于6位')
        return
      }
      // 模拟重置成功
      this.step = 3
    }
  }
}
</script>

<style scoped>
.forget-password-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.forget-password-box {
  width: 450px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  position: relative;
}

.back-link {
  position: absolute;
  top: 20px;
  left: 20px;
  cursor: pointer;
  color: #666;
  font-size: 14px;
}

.back-link:hover {
  color: #FF5202;
}

.title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
  padding-top: 10px;
}

.form-item {
  margin-bottom: 20px;
}

.input-field {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
}

.input-field:focus {
  outline: none;
  border-color: #FF5202;
}

.code-item {
  display: flex;
  gap: 10px;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  width: 110px;
  background: #f0f0f0;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.send-code-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.next-btn {
  width: 100%;
  padding: 12px;
  background: #FF5202;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
  margin-top: 20px;
}

.next-btn:hover {
  background: #e04a02;
}

.success-box {
  text-align: center;
}

.success-icon {
  width: 80px;
  height: 80px;
  background: #52c41a;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 50px;
  color: white;
}

.success-text {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}

.login-btn {
  padding: 12px 40px;
  background: #FF5202;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
</style><template>
  <div class="forget-password-container">
    <div class="forget-password-box">
      <div class="back-link" @click="goBack">
        ← 返回登录
      </div>
      <h2 class="title">找回密码</h2>

      <!-- 步骤1：验证手机号 -->
      <div v-if="step === 1" class="step-form">
        <div class="form-item">
          <input type="text" v-model="formData.username" placeholder="请输入用户账号" class="input-field">
        </div>
        <div class="form-item">
          <input type="tel" v-model="formData.phone" placeholder="请输入绑定手机号" class="input-field">
        </div>
        <div class="form-item code-item">
          <input type="text" v-model="formData.verifyCode" placeholder="验证码" class="input-field code-input">
          <button class="send-code-btn" :disabled="countdown > 0" @click="sendCode">
            {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
          </button>
        </div>
        <button class="next-btn" @click="verifyPhone">下一步</button>
      </div>

      <!-- 步骤2：重置密码 -->
      <div v-if="step === 2" class="step-form">
        <div class="form-item">
          <input type="password" v-model="formData.newPassword" placeholder="请输入新密码" class="input-field">
        </div>
        <div class="form-item">
          <input type="password" v-model="formData.confirmPassword" placeholder="确认新密码" class="input-field">
        </div>
        <button class="next-btn" @click="resetPassword">确认修改</button>
      </div>

      <!-- 步骤3：完成 -->
      <div v-if="step === 3" class="success-box">
        <div class="success-icon">✓</div>
        <p class="success-text">密码重置成功！</p>
        <button class="login-btn" @click="goToLogin">立即登录</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'forgetPassword',
  data() {
    return {
      step: 1,
      formData: {
        username: '',
        phone: '',
        verifyCode: '',
        newPassword: '',
        confirmPassword: ''
      },
      countdown: 0
    }
  },
  methods: {
    goBack() {
      this.$router.push('/login')
    },
    goToLogin() {
      this.$router.push('/login')
    },
    sendCode() {
      if (!this.formData.phone) {
        alert('请输入手机号')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.formData.phone)) {
        alert('请输入正确的手机号')
        return
      }
      // 模拟发送验证码
      alert('验证码已发送：123456（测试用）')
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
    verifyPhone() {
      if (!this.formData.username) {
        alert('请输入用户账号')
        return
      }
      if (!this.formData.phone) {
        alert('请输入手机号')
        return
      }
      if (!this.formData.verifyCode) {
        alert('请输入验证码')
        return
      }
      // 模拟验证成功
      this.step = 2
    },
    resetPassword() {
      if (!this.formData.newPassword) {
        alert('请输入新密码')
        return
      }
      if (this.formData.newPassword !== this.formData.confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }
      if (this.formData.newPassword.length < 6) {
        alert('密码长度不能少于6位')
        return
      }
      // 模拟重置成功
      this.step = 3
    }
  }
}
</script>

<style scoped>
.forget-password-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.forget-password-box {
  width: 450px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  position: relative;
}

.back-link {
  position: absolute;
  top: 20px;
  left: 20px;
  cursor: pointer;
  color: #666;
  font-size: 14px;
}

.back-link:hover {
  color: #FF5202;
}

.title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
  padding-top: 10px;
}

.form-item {
  margin-bottom: 20px;
}

.input-field {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
}

.input-field:focus {
  outline: none;
  border-color: #FF5202;
}

.code-item {
  display: flex;
  gap: 10px;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  width: 110px;
  background: #f0f0f0;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.send-code-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.next-btn {
  width: 100%;
  padding: 12px;
  background: #FF5202;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
  margin-top: 20px;
}

.next-btn:hover {
  background: #e04a02;
}

.success-box {
  text-align: center;
}

.success-icon {
  width: 80px;
  height: 80px;
  background: #52c41a;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 50px;
  color: white;
}

.success-text {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}

.login-btn {
  padding: 12px 40px;
  background: #FF5202;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
</style>