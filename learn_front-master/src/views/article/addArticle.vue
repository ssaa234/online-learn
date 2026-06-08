<template>
  <div class="addArticle">
    <headerPage></headerPage>
    <div class="addArticle-content">
      <div class="addArticle-form">
        <div class="addArticle-title">记笔记</div>
        <div class="addArticle-list">
          <div class="addArticle-item">
            <div style="margin-top:10px;margin-left:10px">标题</div>
            <div style="margin-top:10px;margin-left:10px;margin-bottom:10px">
              <el-input size="mini" v-model="form.title" placeholder="请输入笔记标题"></el-input>
            </div>
          </div>
          <div class="addArticle-item">
            <div style="margin-top:10px;margin-left:10px">所属课程</div>
            <div style="margin-top:10px;margin-left:10px;margin-bottom:10px">
              <el-select
                  size="mini"
                  v-model="form.taskId"
                  :disabled="isTaskDisabled"
                  placeholder="请选择所属课程">
                <el-option
                    v-for="(item,index) in task"
                    :key="index"
                    :label="item.taskName"
                    :value="item.taskId">
                </el-option>
              </el-select>
              <!-- 显示从课程详情页传递的课程名称 -->
              <div v-if="routeTaskName" class="task-tip">
                当前课程：{{ routeTaskName }}
              </div>
            </div>
          </div>
          <div class="addArticle-item">
            <div style="margin-top:10px;margin-left:10px">摘要</div>
            <div style="margin-top:10px;margin-left:10px;margin-bottom:10px">
              <el-input type="textarea" rows=5 v-model="form.articleDesc" size="mini" placeholder="请输入笔记摘要"></el-input>
            </div>
          </div>
          <div class="addArticle-item" style="height:600px">
            <div style="margin-top:10px;margin-left:10px">内容</div>
            <div style="margin-top:10px;margin-left:10px;margin-bottom:10px">
              <quill-editor style="height:450px" ref="myQuillEditor" :options="editorOption"
                            v-model="form.content"/>
              <el-upload class="Quill-uploader" :show-file-list="false" accept="image/*" v-show="0" ref="updateUpload" :multiple="false" :action="uploadImageUrl()"
                         :on-success="handleQillSuccess">
              </el-upload>
            </div>
          </div>
          <div style="margin-bottom:20px">
            <el-button @click="submit" size="mini" type="primary">提交</el-button>
          </div>
        </div>
      </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {getApeMyTaskList, saveApeArticle} from '../../api/api'
import { mixin } from '../../minix/index';
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
export default {
  mixins: [mixin],
  data() {
    return{
      task: [],
      routeTaskId: "",      // 从路由传递过来的课程ID
      routeTaskName: "",    // 从路由传递过来的课程名称
      isTaskDisabled: false, // 课程选择框是否禁用
      form: {
        articleDesc: "",
        content: "",
        title: "",
        taskId: "",
      },
      editorOption: {
        placeholder: "请输入文字内容",
        modules: {
          toolbar: {
            container: [
              ["bold", "italic", "underline", "strike"],
              ["blockquote", "code-block"],
              [{ header: 1 }, { header: 2 }],
              [{ list: "ordered" }, { list: "bullet" }],
              [{ script: "sub" }, { script: "super" }],
              [{ indent: "-1" }, { indent: "+1" }],
              [{ direction: "rtl" }],
              [{ size: ["small", false, "large", "huge"] }],
              [{ header: [1, 2, 3, 4, 5, 6, false] }],
              [{ color: [] }, { background: [] }],
              [{ font: [] }], [{ align: [] }],
              ["clean"], ["image"]
            ],
            handlers: {
              'image': function ( value ) {
                if ( value ) {
                  document.querySelector('.Quill-uploader .el-upload__input').click()
                } else {
                  this.quill.format( 'image', false );
                }
              }
            }
          },
          syntax: {
            highlight: text => {
              return hljs.highlightAuto(text).value;
            }
          }
        }
      },
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    getApeMyTaskList() {
      var param = {
        state: 0
      }
      getApeMyTaskList(param).then(res => {
        if (res.code == 1000) {
          this.task = res.data
          // 如果有从课程详情页传递的课程ID，自动选中并禁用选择框
          if (this.routeTaskId) {
            // 检查传递的课程ID是否在任务列表中
            const taskExists = this.task.some(item => item.taskId === this.routeTaskId)
            if (taskExists) {
              this.form.taskId = this.routeTaskId
              this.isTaskDisabled = true
            } else {
              this.$message({
                message: '该课程不存在或您未报名',
                type: 'warning'
              })
            }
          }
        }
      })
    },
    submit() {
      if (!this.form.title) {
        this.$message({
          message: '请输入笔记标题',
          type: 'warning'
        })
        return
      }
      if (!this.form.content) {
        this.$message({
          message: '请输入笔记内容',
          type: 'warning'
        })
        return
      }
      if (!this.form.articleDesc) {
        this.$message({
          message: '请输入笔记摘要',
          type: 'warning'
        })
        return
      }
      if (!this.form.taskId) {
        this.$message({
          message: '请选择所属课程',
          type: 'warning'
        })
        return
      }
      saveApeArticle(this.form).then(res => {
        if(res.code == 1000) {
          this.$message({
            message: '发布成功',
            type: 'success'
          })
          // 成功跳转回笔记列表
          setTimeout(() => {
            this.$router.push("/myArticle")
          }, 1500)
        } else {
          this.$message({
            message: res.message || '发布失败',
            type: 'error'
          })
        }
      })
    },
    handleQillSuccess(res){
      let _this = this;
      if(res.code == 1000){
        _this.$notify({
          title: '上传成功',
          type: 'success'
        });
        let imgurl = this.$store.state.HOST + res.message
        let quill = this.$refs.myQuillEditor.quill
        let position = quill.getSelection().index
        quill.insertEmbed(position, 'image', imgurl)
        quill.setSelection(position + 1)
      }else{
        _this.$notify({
          title: '上传失败',
          type: 'error'
        });
      }
    },
  },
  created() {
    // 获取路由传递的参数
    this.routeTaskId = this.$route.query.taskId || ""
    this.routeTaskName = this.$route.query.taskName || ""
    console.log('接收到的课程ID:', this.routeTaskId)
    console.log('接收到的课程名称:', this.routeTaskName)

    // 如果有课程ID，设置为禁用状态并自动填充
    if (this.routeTaskId) {
      this.isTaskDisabled = true
      this.form.taskId = this.routeTaskId
    }
  },
  mounted() {
    this.getApeMyTaskList()
  }
}
</script>

<style scoped>
@import url("../../assets/css/article/addArticle.css");

/* 新增样式 */
.task-tip {
  font-size: 12px;
  color: #20B486;
  margin-top: 5px;
}
</style>