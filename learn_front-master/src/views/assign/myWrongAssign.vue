<template>
  <div class="assignInfo">
    <headerPage></headerPage>
    <div class="assignInfo-content">
        <el-collapse style="width:70%;margin-top:30px" v-model="activeNames">
            <el-collapse-item v-for="(item,index) in data" :key="index" :title="item.taskName +' - ' + item.chapterName +' - 章节作业'" :name="index">
                <div class="assignInfo-top" style="width:100%">
                    <div class="assignInfo-list" style="width:100%">
                        <div class="assignInfo-item" v-for="(e,s) in item.list" :key="s">
                            <div style="margin-top:10px;margin-left:10px">{{s + 1}}.{{e.title}} 
                                <span style="color:red" v-if="e.score != e.point">正确答案：{{e.answer}}</span>
                            </div>
                            <div style="margin-top:10px;margin-left:10px;margin-bottom:10px">
                                <el-radio-group v-model="e.solution" v-if="e.type == 0">
                                    <el-radio v-for="(m,d) in JSON.parse(e.content)" :key="d" :label="m.value">{{m.value}}.{{m.option}}</el-radio>
                                </el-radio-group>
                                <el-checkbox-group v-model="e.solution" v-if="e.type == 1">
                                    <el-checkbox v-for="(m,d) in JSON.parse(e.content)" :key="d" :label="m.value">{{m.value}}.{{m.option}}</el-checkbox>
                                </el-checkbox-group>
                                <el-input v-model="e.solution" v-if="e.type == 2" size="mini" placeholder="请输入答案"></el-input>
                                <el-radio-group v-model="e.solution" v-if="e.type == 3">
                                    <el-radio label="正确">正确</el-radio>
                                    <el-radio label="错误">错误</el-radio>
                                </el-radio-group>
                            </div>
                        </div>
                    </div>
                </div>
            </el-collapse-item>
        </el-collapse>
        
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
  import {getMyApeHomework,getWrongWork} from "../../api/api"
  import headerPage from "../../components/header/header"
  import bottomPage from "../../components/bottom/bottom"
  export default {
    data() {
      return{
        data: [],
        activeNames: '0'
      }
    },
    components: {
      headerPage,
      bottomPage
    },
    methods: {
        query() {
            getMyApeHomework().then(res => {
                if(res.code == 1000) {
                    this.data = res.data
                    for (let i = 0;i<this.data.length;i++) {
                        var item = this.data[i]
                        getWrongWork({id:item.chapterId}).then(res1 => {
                            if (res1.code == 1000) {
                                item.list = res1.data
                            }
                        })
                    }
                }
            })
        },
    },
    created() {
     
    },
    mounted() {
      window.scrollTo({
         top: 0,
         behavior: 'smooth'
      });
      this.query()
    }
 }
</script>

<style scoped>
  @import url("../../assets/css/assign/assignInfo.css");
</style>