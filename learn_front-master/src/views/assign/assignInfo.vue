<template>
  <div class="assignInfo">
    <headerPage></headerPage>
    <div class="assignInfo-content">
        <div class="assignInfo-top">
            <div class="assignInfo-title">
                 {{chapter.taskName}} - {{chapter.name}} - 章节作业
            </div>
            <div class="assignInfo-state">
               {{flag?'已提交':'未提交'}} 总分：{{total}}分  得分：{{defen}}分
            </div>
            <div class="assignInfo-list">
                <div class="assignInfo-item" v-for="(item,index) in homework" :key="index">
                    <div style="margin-top:10px;margin-left:10px">{{index + 1}}.{{item.title}} 
                        <span style="color:red" v-if="flag && item.score != item.point">正确答案：{{item.answer}}</span>
                        <span style="color:green" v-if="flag && item.score == item.point">正确答案：{{item.answer}}</span>
                    </div>
                    <div style="margin-top:10px;margin-left:10px;margin-bottom:10px">
                        <el-radio-group v-model="item.solution" v-if="item.type == 0">
                            <el-radio v-for="(e,s) in item.content" :key="s" :label="e.value">{{e.value}}.{{e.option}}</el-radio>
                        </el-radio-group>
                        <el-checkbox-group v-model="item.solution" v-if="item.type == 1">
                            <el-checkbox v-for="(e,s) in item.content" :key="s" :label="e.value">{{e.value}}.{{e.option}}</el-checkbox>
                        </el-checkbox-group>
                        <el-input v-model="item.solution" v-if="item.type == 2" size="mini" placeholder="请输入答案"></el-input>
                        <el-radio-group v-model="item.solution" v-if="item.type == 3">
                            <el-radio label="正确">正确</el-radio>
                            <el-radio label="错误">错误</el-radio>
                        </el-radio-group>
                    </div>
                </div>
                <div style="margin-bottom:20px">
                    <el-button v-if="!flag" @click="submit" size="mini" type="primary">提交</el-button>
                </div>
            </div>
        </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
  import {getApeChapterById,getApeHomeworkStudentList,saveApeHomeworkStudent,getApeHomeworkStudentFlag} from "../../api/api"
  import headerPage from "../../components/header/header"
  import bottomPage from "../../components/bottom/bottom"
  export default {
    data() {
      return{
        chapterId: "",
        chapter: {},
        homework: [],
        total: 0,
        defen: 0,
        flag: false
      }
    },
    components: {
      headerPage,
      bottomPage
    },
    methods: {
      getApeChapterById() {
          getApeChapterById({id:this.chapterId}).then(res => {
              if(res.code == 1000) {
                  this.chapter = res.data
              }
          })
      },
      getApeHomeworkStudentList() {
          var param = {
              chapterId: this.chapterId
          }
          getApeHomeworkStudentList(param).then(res => {
              if (res.code == 1000) {
                  this.homework = res.data
                  for(let i = 0;i<this.homework.length;i++) {
                      var item = this.homework[i]
                      this.total = this.total + item.score
                      this.defen = this.defen + item.point
                      if (item.type == "0" || item.type == "1") {
                          if (item.content) {
                              item.content = JSON.parse(item.content)
                          }
                          if(item.type == 1) {
                              if(item.solution) {
                                item.solution = item.solution.split(",")
                              } else {
                                item.solution = []
                              }
                          }
                      }
                  }
              }
          })
      },
      submit() {
        this.$confirm('确定要提交作业内容吗？','提醒', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
            }).then(() => {
                for(let i = 0;i<this.homework.length;i++) {
                    var item = this.homework[i]
                    if(item.type == 1) {
                        item.solution = item.solution.join(",")
                    }
                }
                var param = {
                    homework: this.homework
                }
                saveApeHomeworkStudent(param).then(res => {
                    if (res.code == 1000) {
                        this.$message({
                            type: 'success',
                            message: '提交成功!'
                        });
                        this.$router.push("/taskInfo?id="+this.chapter.taskId)
                    }
                })
            }).catch(() => {
                    
        });
      },
      getApeHomeworkStudentFlag() {
          getApeHomeworkStudentFlag({id:this.chapterId}).then(res => {
              if(res.code == 1000) {
                  this.flag = true
              } else {
                  this.flag = false
              }
          })
      }
    },
    created() {
     
    },
    mounted() {
      window.scrollTo({
         top: 0,
         behavior: 'smooth'
      });
      var chapterId = this.$route.query.id
      if (chapterId) {
          this.chapterId = chapterId
      }
      this.getApeChapterById()
      this.getApeHomeworkStudentList()
      this.getApeHomeworkStudentFlag()
    }
 }
</script>

<style scoped>
  @import url("../../assets/css/assign/assignInfo.css");
</style>