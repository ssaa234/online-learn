<template>
  <div class="centerPage">
    <headerPage></headerPage>
    <div class="centerPage-content">
      <el-card class="box-card">
        <div class="content">
          <div class="master">
            <el-form style="margin-right:20px" :model="user" :rules="rules" ref="ruleForm" label-width="100px">
              <el-form-item label="用户名" prop="userName">
                <el-input size="mini" v-model="user.userName"></el-input>
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input size="mini" v-model="user.email"></el-input>
              </el-form-item>
              <el-form-item label="联系电话" prop="tel">
                <el-input size="mini" v-model="user.tel"></el-input>
              </el-form-item>
              <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="user.sex">
                  <el-radio label="0">男</el-radio>
                  <el-radio label="1">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日" prop="birth">
                <el-date-picker
                    v-model="user.birth"
                    type="date"
                    placeholder="请选择生日"
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
                <el-select size="mini" v-model="user.country" clearable>
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
                  :src="$store.state.HOST + user.avatar"
                  :preview-src-list="avatar">
              </el-image>
            </div>
            <div class="btns">
              <div>
                <el-upload
                    ref="upload"
                    :action="uploadAvatarUrl()+ '/'+ this.user.id"
                    :show-file-list="false"
                    :before-upload="beforeAvatorUpload"
                    :on-success="handleAvatorSuccess"
                    accept="image/*"
                >
                  <el-button style="margin-top:15px" size="mini" icon="el-icon-picture-outline-round">修改头像</el-button>
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

    <el-dialog
        title="修改密码"
        :visible.sync="passwordDialogVisible"
        width="30%"
        :before-close="handlePasswordClose">
      <span>请输入{{user.userName}}的旧密码：</span>
      <el-input style="margin-top:10px" show-password v-model="oldPassword" size="mini" autocomplete="off"></el-input>
      <span>请输入{{user.userName}}的新密码：</span>
      <el-input style="margin-top:10px" show-password v-model="newPassword" size="mini" autocomplete="off"></el-input>
      <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="handlePasswordClose">取 消</el-button>
          <el-button size="mini" type="primary" @click="passwordSubmit">确 定</el-button>
        </span>
    </el-dialog>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {mixin} from "../../minix";
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
import {getUserInfo,setUserInfo,setUserAvatar,changePassword,getApeSchoolList,getApeMajorList} from '../../api/api'
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
    // 邮箱验证函数（非必填，有值时才验证格式）
    var checkEmail = (rule, value, callback) => {
      if (!value) {
        callback();  // 为空时直接通过
      } else {
        const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (reg.test(value)) {
          callback();
        } else {
          callback(new Error('请输入正确的邮箱地址'));
        }
      }
    };
    // 生日验证函数（必填）
    var checkBirth = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择生日'));
      } else {
        callback();
      }
    };
    // 年龄验证函数（必填，由生日自动计算）
    var checkAge = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error('请先选择生日'));
      } else {
        callback();
      }
    };
    return{
      school: [],
      major: [],
      user: {},
      avatar: [],
      rules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
        ],
        email: [
          { required: false, validator: checkEmail, trigger: 'blur' }
        ],
        tel: [
          { required: true, validator: checkPhone, message: '请输入正确的联系电话', trigger: 'blur' },
        ],
        sex: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        birth: [
          { required: true, validator: checkBirth, trigger: 'change' }
        ],
        age: [
          { required: true, validator: checkAge, trigger: 'change' }
        ]
      },
      oldPassword: "",
      newPassword: "",
      passwordDialogVisible: false,
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    // 根据生日计算年龄
    calculateAge() {
      if (this.user.birth) {
        let birthDate;
        // 处理不同的日期格式
        if (typeof this.user.birth === 'string') {
          // 如果是 "yyyy-MM-dd HH:mm:ss" 格式，取前10位
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
          this.user.age = age;
          // 手动触发表单验证，清除年龄错误提示
          this.$nextTick(() => {
            this.$refs.ruleForm.validateField('age');
            this.$refs.ruleForm.validateField('birth');
          });
        } else {
          this.user.age = '';
        }
      } else {
        this.user.age = '';
      }
    },
    getApeSchoolList() {
      getApeSchoolList().then(res=> {
        if(res.code == 1000) {
          this.school = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    getApeMajorList() {
      getApeMajorList().then(res => {
        if(res.code == 1000) {
          this.major = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    handlePasswordClose() {
      this.$bus.$emit('password', false)
    },
    passwordSubmit() {
      var param = {
        id: this.user.id,
        password: this.oldPassword,
        newPassword: this.newPassword
      }
      changePassword(param).then(res => {
        if(res.code == 1000) {
          this.$notify.success({
            title: '成功',
            message: "密码修改成功"
          });
          this.$bus.$emit('password', false)
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    submit() {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid) {
          // 准备提交的数据
          const submitData = JSON.parse(JSON.stringify(this.user));

          // 处理生日：转换为 datetime 格式，字段名改为 birth
          if (submitData.birth) {
            let dateStr = submitData.birth;
            if (dateStr.includes(' ')) {
              dateStr = dateStr.split(' ')[0];
            }
            submitData.birth = dateStr + ' 00:00:00';
          } else {
            submitData.birth = null;
          }

          // 删除不需要提交的字段
          /*delete submitData.age;
          delete submitData.birthday;  // 如果存在的话*/

          console.log('提交数据:', submitData);

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
      getUserInfo().then(res => {
        if(res.code == 1000) {
          this.user = res.data
          this.user.sex = res.data.sex + ""
          this.avatar[0] = this.$store.state.HOST + this.user.avatar

          // 处理生日：从 datetime 中提取日期部分，字段名改为 birth
          if (this.user.birth) {
            let birthStr = this.user.birth;
            // 如果是字符串且包含空格，取日期部分
            if (typeof birthStr === 'string' && birthStr.includes(' ')) {
              birthStr = birthStr.split(' ')[0];
            } else if (typeof birthStr !== 'string') {
              // 如果是 Date 对象或时间戳，转换格式
              const date = new Date(birthStr);
              if (!isNaN(date.getTime())) {
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                birthStr = `${year}-${month}-${day}`;
              }
            }
            this.user.birth = birthStr;
            this.calculateAge();
          }
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
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
    // 监听collapse
    this.$bus.$on('password', res=>{
      this.passwordDialogVisible = res
    })
    this.getUserInfo()
    this.getApeSchoolList()
    this.getApeMajorList()
  }
}
</script>

<style scoped>
.centerPage {
  width: 100%;
  height: 100%;
  background-image: url('../../assets/image/index/index_back.png');
}
.centerPage-content {
  width: 100%;
  display: flex;
  justify-content: center;
}
.box-card {
  margin-top: 30px;
  margin-bottom: 30px;
  width: 70%;
}
.content {
  width: 100%;
  height: 550px;
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