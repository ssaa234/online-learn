<template>
  <div class="center">
    <el-card class="box-card">
        <div class="content">
            <div class="master">
              <el-form style="margin-right:20px" :model="user" :rules="rules" ref="ruleForm" label-width="140px">
                <el-form-item label="用户名" prop="userName">
                  <el-input size="mini" v-model="user.userName"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input size="mini" v-model="user.email"></el-input>
                </el-form-item>
                <el-form-item label="联系电话" prop="tel">
                  <el-input size="mini" v-model="user.tel"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="tel">
                  <el-radio-group v-model="user.sex">
                    <el-radio label="0">男</el-radio>
                    <el-radio label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="生日" prop="birth">
                  <el-date-picker
                      v-model="user.birth"
                      type="date"
                      placeholder="选择生日"
                      value-format="yyyy-MM-dd"
                      format="yyyy-MM-dd"
                      size="mini"
                      style="width: 100%"
                      @change="calculateAge">
                  </el-date-picker>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                  <el-input size="mini" v-model="user.age" disabled placeholder="自动计算"></el-input>
                </el-form-item>
                <el-form-item label="国家" prop="country">
                  <el-select size="mini" v-model="user.country">
                    <el-option label="中国" value="中国"></el-option>
                    <el-option label="日本" value="日本"></el-option>
                    <el-option label="韩国" value="韩国"></el-option>
                    <el-option label="美国" value="美国"></el-option>
                    <el-option label="俄罗斯" value="俄罗斯"></el-option>
                    <el-option label="印度" value="印度"></el-option>
                    <el-option label="巴西" value="巴西"></el-option>
                    <el-option label="新加坡" value="新加坡"></el-option>
                    <el-option label="加拿大" value="加拿大"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="个人介绍" prop="introduction">
                  <el-input
                      type="textarea"
                      v-model="user.remark"
                      :rows="4"
                      placeholder="请输入个人介绍"
                      size="mini"
                      maxlength="500"
                      show-word-limit>
                  </el-input>
                </el-form-item>
              </el-form>
              <div class="submit">
                  <el-button type="primary" plain size="mini" @click="submit">保存</el-button>
              </div>
            </div>
            <div class="slave">
                <div class="img">
                  <el-image 
                    style="object-fit: cover;width: 300px; height: 300px;overflow: hidden;border-radius: 50%;"
                    :src="$store.state.configure.HOST + user.avatar" 
                    :preview-src-list="avatar">
                  </el-image>
                </div>
                <div class="btns">
                  <div>
                    <el-upload
                      ref="upload"
                      :action="uploadAvatarUrl()+ '/'+ this.user.id"
                      :show-file-list="false"
                      :multiple="false"
                      :before-upload="beforeAvatorUpload"
                      :on-success="handleAvatorSuccess"
                      accept="image/*"
                      >
                      <el-button size="mini" icon="el-icon-picture-outline-round">修改头像</el-button>
                    </el-upload>
                  </div>
                  <div style="margin-top:15px">
                    <el-button size="mini" icon="el-icon-key" @click="changePassword">修改密码</el-button>
                  </div>
                </div>
            </div>
        </div>
    </el-card>
  </div>
</template>

<script>
  import {mixin} from "../../../minix";
  import {getUser,setUserInfo,setUserAvatar} from '../../../api/api' 
  export default {
    mixins: [mixin],
    data() {
      var checkPhone = (rule, value, callback) => {
          if (!value) {
              return callback(new Error('请输入联系电话'));
          } else {
              const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
              if (reg.test(value)) {
                  callback();
              } else {
                  return callback(new Error('请输入正确的联系电话'));
              }
          }
      };
      return{
        user: {
          avatar: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2Fb4a87154-18b6-4163-ac80-f4dc4bf58d09%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1693364028&t=c518c6c4a6ee2009b961f712a57c211f",
          age: ""
        },
        avatar: ["https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2Fb4a87154-18b6-4163-ac80-f4dc4bf58d09%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1693364028&t=c518c6c4a6ee2009b961f712a57c211f"],
        rules: {
          userName: [
            { required: true, message: '请输入用户名', trigger: 'blur' },
          ],
          email: [
            { required: true, message: '请输入邮箱地址', trigger: 'blur' },
            { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
          ],
          tel: [
            { required: true, validator:checkPhone, message: '请输入正确的联系电话', trigger: 'blur' },
          ],
          age: [
            { required: false }
          ],
          birth: [
            { required: false }
          ],
        },
      }
    },
    methods: {
      // 根据生日计算年龄
      calculateAge() {
        if (this.user.birth) {  // 注意这里改成 this.user
          let birthDate;
          // 处理不同的日期格式
          if (typeof this.user.birth === 'string') {
            const dateStr = this.user.birth.includes(' ') ? this.user.birth.split(' ')[0] : this.user.birth;
            birthDate = new Date(dateStr);
          } else {
            birthDate = new Date(this.user.birth);
          }

          if (!isNaN(birthDate.getTime())) {
            const today = new Date();
            let age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
              age--;
            }
            this.user.age = age;  // 这里改成 this.user.age，不是 this.form.age
          } else {
            this.user.age = '';
          }
        } else {
          this.user.age = '';
        }
      },
      submit() {
        this.$refs["ruleForm"].validate((valid) => {
          if (valid) {
            // 准备提交的数据
            const submitData = JSON.parse(JSON.stringify(this.user));

            // 处理生日格式（如果需要）
            if (submitData.birth) {
              let dateStr = submitData.birth;
              if (dateStr.includes(' ')) {
                dateStr = dateStr.split(' ')[0];
              }
              submitData.birth = dateStr + ' 00:00:00';
            }

            setUserInfo(submitData).then(res => {
              if(res.code == 1000) {
                this.$message({
                  type: 'success',
                  message: '保存成功!'
                });
                this.getUserInfo()
              } else {
                this.$notify.error({
                  title: '错误',
                  message: res.message
                });
              }
            })
          } else {
            return false;
          }
        });
      },
      getUserInfo() {
        getUser().then(res => {
          if(res.code == 1000) {
            this.user = res.data
            this.user.sex = res.data.sex + ""
            this.avatar[0] = this.$store.state.configure.HOST + this.user.avatar

            // 如果生日存在，计算年龄
            if (this.user.birth) {
              this.calculateAge();
            }

            this.setUser()
          } else {
            this.$notify.error({
              title: '错误',
              message: res.message
            });
          }
        })
      },
      setUser() {
        this.$store.commit('user/setUser', JSON.stringify(this.user))
      },
      changePassword() {
        //修改密码
        this.$bus.$emit('password', true)
      },
      handleAvatorSuccess(res){
        let _this = this;
        if(res.code == 1000){
            _this.$message({
              type: 'success',
              message: '上传成功!'
            });
            this.getUserInfo()
        }else{
          _this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      },
    },
    created() {
     
    },
    mounted() {
      this.getUserInfo()
    }
 }
</script>

<style   scoped>
  .content {
      width: 100%;
      height: 570px;
      display: flex;
      flex-direction: row;
      font-family: "黑体";
  }
  .master {
      width: 60%;
      border-right: 1px solid #E5E5E5;
  }
  .submit {
    width: 100%;
    text-align: center;
  }
  .slave {
      width: 40%;
  }
  .img {
      width: 100%;
      height: 75%;
      display: flex;
      justify-content: center;
      align-items: center;
  }
  .btns {
      width: 100%;
      height: 25%;
      display: flex;
      flex-direction: column;
      align-items: center;
  }
</style>