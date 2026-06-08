<template>
  <div>
    <el-dialog title="新增课程" width="40%" :destroy-on-close="true" :visible.sync="addVisible" :before-close="handleClose">
      <el-form :model="form" :rules="rules" ref="ruleForm">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                课程名称:
                </span>
            <div style="width:100%">
              <el-form-item prop="name" style="margin-bottom:0">
                <el-input v-model="form.name" size="mini" placeholder="请输入课程名称" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                课程占比:
                </span>
            <div style="width:100%">
              <el-form-item prop="proportion" style="margin-bottom:0">
                <el-input type="number" min="0" max="100" v-model="form.proportion" size="mini" placeholder="请输入视频和作业分数所占比例" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col v-if="flag == 1" :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                教师名称:
                </span>
            <div style="width:100%">
              <el-form-item prop="teacherId" style="margin-bottom:0">
                <el-select clearable style="margin-top:10px" size="mini" v-model="form.teacherId" placeholder="请选择">
                  <el-option v-for="(item,index) in teacher" :key="index"
                             :label="item.userName"
                             :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>

          <!-- 所属学段 -->
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                所属学段:
                </span>
            <div style="width:100%">
              <el-form-item prop="gradeLevel" style="margin-bottom:0">
                <el-select clearable style="margin-top:10px" size="mini" v-model="form.gradeLevel" placeholder="请选择学段" @change="onGradeLevelChange">
                  <el-option label="小学" value="小学"></el-option>
                  <el-option label="初中" value="初中"></el-option>
                  <el-option label="高中" value="高中"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>

          <!-- 所属年级（学段不为其他时显示） -->
          <el-col v-if="showGrade" :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                所属年级:
                </span>
            <div style="width:100%">
              <el-form-item prop="grade" style="margin-bottom:0">
                <el-select clearable style="margin-top:10px" size="mini" v-model="form.grade" placeholder="请选择年级">
                  <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>

          <!-- 所属教材（学段不为其他时显示） -->
          <el-col v-if="showTextbook" :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                所属教材:
                </span>
            <div style="width:100%">
              <el-form-item prop="textbook" style="margin-bottom:0">
                <el-input v-model="form.textbook" size="mini" placeholder="请输入教材" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>

          <!-- 所属分类（仅当学段为"其他"时显示） -->
          <el-col v-if="showClassification" :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                所属分类:
                </span>
            <div style="width:100%">
              <el-form-item prop="classification" style="margin-bottom:0">
                <el-select clearable style="margin-top:10px" size="mini" v-model="form.classification" placeholder="请选择">
                  <el-option v-for="(item,index) in classification" :key="index"
                             :label="item.name"
                             :value="item.name">
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>

          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                封面:
                </span>
            <div style="width:100%">
              <el-form-item prop="image" style="margin-bottom:0">
                <el-upload
                    :file-list="fileList"
                    ref="upload"
                    :action="uploadImageUrl()"
                    accept="image/*"
                    :multiple="false"
                    :limit="1"
                    :before-upload="beforeAvatorUpload"
                    list-type="picture-card"
                    :on-preview="handlePictureCardPreview"
                    :on-exceed="handleExceed"
                    :on-success="handleAvatorSuccess"
                    :on-remove="handleRemove">
                  <i class="el-icon-plus"></i>
                </el-upload>
                <el-dialog :visible.sync="dialogVisible">
                  <img width="100%" :src="dialogImageUrl" alt="">
                </el-dialog>
              </el-form-item>
            </div>
          </el-col>
          <el-col style="margin-top:35px" :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                课程描述:
                </span>
            <div style="width:100%">
              <el-form-item prop="taskDescribe" style="margin-bottom:0">
                <el-input type="textarea" v-model="form.taskDescribe" size="mini" placeholder="请输入课程描述" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col style="margin-top:25px" :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span class="search-title">
                备注:
                </span>
            <div style="width:100%">
              <el-form-item prop="remark" style="margin-bottom:0">
                <el-input type="textarea" v-model="form.remark" size="mini" placeholder="请输入备注" autocomplete="off"></el-input>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" type="primary" @click="submit">确 定</el-button>
        <el-button size="mini" @click="handleClose">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {mixin} from "../../../minix";
import {saveApeTask,getUserListByType,getApeClassificationList} from '../../../api/api'
export default {
  mixins: [mixin],
  data() {
    return{
      formLabelWidth: '80px',
      form: {
        name: "",
        taskDescribe: "",
        teacherId: "",
        teacherName: "",
        image: "",
        state: 0,
        classification: "",
        num: "",
        remark: "",
        proportion: "",
        gradeLevel: "",   // 学段
        grade: "",         // 年级
        textbook: "",      // 教材
        type: 0,
      },
      fileList: [],
      dialogImageUrl: '',
      dialogVisible: false,
      showGrade: false,         // 是否显示年级
      showTextbook: false,      // 是否显示教材
      showClassification: false, // 是否显示所属分类（仅其他学段显示）
      gradeOptions: [],         // 年级选项
      rules: {
        proportion: [
          { required: true, message: '请输入视频和作业分数占比', trigger: 'blur' },
        ],
        name: [
          { required: true, message: '请输入课程名称', trigger: 'blur' },
        ],
        taskDescribe: [
          { required: true, message: '请输入课程描述', trigger: 'blur' },
        ],
        teacherId: [
          { required: true, message: '请选择教师', trigger: 'blur' },
        ],
        gradeLevel: [
          { required: true, message: '请选择学段', trigger: 'change' },
        ],
        classification: [
          { required: false, message: '请选择所属分类', trigger: 'blur' },
        ],
        image: [
          { required: true, message: '请上传封面图片', trigger: 'blur' },
        ],
      },
      teacher: [],
      classification: []
    }
  },
  props: ['addVisible','flag'],
  methods: {
    // 学段变化
    onGradeLevelChange(val) {
      // 重置年级、教材、分类
      this.form.grade = ''
      this.form.textbook = ''
      this.form.classification = ''

      if (val === '其他') {
        // 学段为"其他"：显示分类，隐藏年级和教材
        this.showGrade = false
        this.showTextbook = false
        this.showClassification = true
        // 分类设为必填
        this.rules.classification[0].required = true
      } else if (val) {
        // 学段为小学/初中/高中：显示年级和教材，隐藏分类
        this.showGrade = true
        this.showTextbook = true
        this.showClassification = false
        // 分类设为非必填
        this.rules.classification[0].required = false
        this.setGradeOptions(val)
      } else {
        // 未选择学段
        this.showGrade = false
        this.showTextbook = false
        this.showClassification = false
      }
    },

    // 根据学段设置年级选项
    setGradeOptions(gradeLevel) {
      const gradeMap = {
        '小学': [
          { value: '一年级', label: '一年级' },
          { value: '二年级', label: '二年级' },
          { value: '三年级', label: '三年级' },
          { value: '四年级', label: '四年级' },
          { value: '五年级', label: '五年级' },
          { value: '六年级', label: '六年级' }
        ],
        '初中': [
          { value: '七年级', label: '七年级' },
          { value: '八年级', label: '八年级' },
          { value: '九年级', label: '九年级' }
        ],
        '高中': [
          { value: '高一', label: '高一' },
          { value: '高二', label: '高二' },
          { value: '高三', label: '高三' }
        ]
      }
      this.gradeOptions = gradeMap[gradeLevel] || []
    },

    handleAvatorSuccess(res) {
      let _this = this;
      if(res.code == 1000){
        _this.$message({
          type: 'success',
          message: '上传成功!'
        });
        this.fileList.push({url:this.$store.state.configure.HOST + res.message})
        this.form.image = res.message
      }else{
        _this.$notify.error({
          title: '错误',
          message: res.message
        });
      }
    },
    handleRemove(file, fileList) {
      this.form.image = ""
      this.fileList = []
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    getUserListByType() {
      getUserListByType({type:1}).then(res => {
        if(res.code == 1000) {
          this.teacher = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    getApeClassificationList() {
      getApeClassificationList().then(res => {
        if(res.code == 1000) {
          this.classification = res.data
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
          // 如果学段不是"其他"，清空分类；如果是"其他"，清空年级和教材
          if (this.form.gradeLevel === '其他') {
            this.form.grade = ''
            this.form.textbook = ''
          } else {
            this.form.classification = ''
          }

          if(this.flag == 2) {
            this.form.type = 1
          }
          saveApeTask(this.form).then(res => {
            if(res.code == 1000) {
              this.$notify.success({
                title: '成功',
                message: "保存成功"
              });
              this.handleClose();
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
    handleClose() {
      this.form = {
        name: "",
        taskDescribe: "",
        teacherId: "",
        teacherName: "",
        image: "",
        state: 0,
        classification: "",
        num: "",
        remark: "",
        proportion: "",
        gradeLevel: "",
        grade: "",
        textbook: "",
        type: 0,
      },
          this.fileList = []
      this.showGrade = false
      this.showTextbook = false
      this.showClassification = false
      this.gradeOptions = []
      // 恢复分类为必填的默认状态
      this.rules.classification[0].required = true
      this.$emit("addFalse")
    },

  },
  created() {

  },
  mounted() {
    this.getUserListByType()
    this.getApeClassificationList()
    if (this.flag == 1) {
      this.form.type = 0
    } else {
      this.form.type = 1
    }
  }
}
</script>

<style scoped>
.el-col {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-top: 12px
}
.search-title {
  font-family: '黑体';
  float: right;
  white-space: nowrap;
  font-size: 14px;
  width: 84px;
  text-align: right;
}
.el-tree {
  border: 1px solid #BDC1C9;
}
</style>