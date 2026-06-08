<template>
  <div class="task">
    <headerPage></headerPage>
    <div class="task-page-content">
      <div class="task-page-search">
        <div class="task-search-item">
          <div>课程名称：</div>
          <div>
            <el-input size="mini" v-model="search.name" placeholder="请输入课程名称"></el-input>
          </div>
        </div>
        <div class="task-search-item">
          <div>教师名称：</div>
          <div>
            <el-input size="mini" v-model="search.teacherName" placeholder="请输入教师名称"></el-input>
          </div>
        </div>
        <div class="task-search-item">
          <div>学段：</div>
          <div>
            <el-select size="mini" v-model="search.educationStage" placeholder="请选择学段" @change="handleEducationStageChange" clearable>
              <el-option label="小学" value="primary"></el-option>
              <el-option label="初中" value="junior"></el-option>
              <el-option label="高中" value="senior"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </div>
        </div>
        <div class="task-search-item" v-if="showTextbookSelect">
          <div>教材：</div>
          <div>
            <el-input size="mini" v-model="search.textbook" placeholder="请输入教材"></el-input>
          </div>
        </div>
        <div class="task-search-item" v-if="showGradeSelect">
          <div>年级：</div>
          <div>
            <el-select size="mini" v-model="search.grade" placeholder="请选择年级" clearable>
              <el-option
                  v-for="item in currentGradeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </div>
        </div>
        <div class="task-search-item">
          <el-button icon="el-icon-search" size="mini" type="primary" plain @click="searchPage()">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" type="warning" plain @click="refresh()">重置</el-button>
        </div>

        <div class="task-search-item" style="width:100%;flex-wrap: wrap;" v-if="showClassificationNav">
          <span style="margin-right:10px">分类：</span>
          <span class="task-search-type" @click="searchClass(item.name)" v-for="(item,index) in classification" :key="index">{{item.name}}</span>
        </div>
      </div>
      <div class="task-font">课程列表</div>
      <div class="task-page-list">
        <div @click="toTaskInfo(item.id)" v-for="(item,index) in tableData" :key="index" class="task-page-item">
          <div class="task-page-image">
            <img style="width:100%;height:100%" :src="$store.state.HOST + item.image">
          </div>
          <div class="task-page-name">
            <div class="task-page-name-one">{{item.name}}</div>
            <div class="task-page-name-two">{{item.taskDescribe}}</div>
          </div>
          <div class="task-page-user">
            <div>讲师：{{item.teacherName}}</div>
            <div>
              学生：
              <i class="el-icon-star-off"></i>
              {{item.num}}
            </div>
          </div>
        </div>
      </div>
      <div class="task-page-fenye">
        <el-pagination
            background
            :page-size="search.pageSize"
            layout="prev, pager, next"
            @current-change="handleCurrentChange"
            :total="total">
        </el-pagination>
      </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {getApeTaskPage,getApeClassificationList} from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
export default {
  data() {
    return{
      search: {
        name: "",
        state: 0,
        teacherName: "",
        educationStage: "",
        textbook: "",
        grade: "",
        classification: "",
        pageSize: 12,
        pageNumber: 1,
        type: 0,
      },
      primaryGradeOptions: [
        { value: '一年级', label: '一年级' },
        { value: '二年级', label: '二年级' },
        { value: '三年级', label: '三年级' },
        { value: '四年级', label: '四年级' },
        { value: '五年级', label: '五年级' },
        { value: '六年级', label: '六年级' }
      ],
      juniorGradeOptions: [
        { value: '七年级', label: '七年级' },
        { value: '八年级', label: '八年级' },
        { value: '九年级', label: '九年级' }
      ],
      seniorGradeOptions: [
        { value: '高一', label: '高一' },
        { value: '高二', label: '高二' },
        { value: '高三', label: '高三' }
      ],
      currentGradeOptions: [],
      showTextbookSelect: false,
      showGradeSelect: false,
      showClassificationNav: false,
      tableData: [],
      classification: [],
      total: 0
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    handleEducationStageChange() {
      this.search.textbook = '';
      this.search.grade = '';
      this.search.classification = '';

      this.showTextbookSelect = false;
      this.showGradeSelect = false;
      this.showClassificationNav = false;

      if (this.search.educationStage === 'other') {
        this.showClassificationNav = true;
      } else if (this.search.educationStage) {
        this.currentGradeOptions = this[`${this.search.educationStage}GradeOptions`];
        this.showTextbookSelect = true;
        this.showGradeSelect = true;
      }
    },
    searchPage() {
      this.search.pageNumber = 1
      this.query()
    },
    searchClass(name) {
      this.search.pageNumber = 1
      this.search.classification = name
      this.query()
    },
    query() {
      // 构建查询参数，字段名要与后端一致
      const params = {
        name: this.search.name,
        teacherName: this.search.teacherName,
        state: this.search.state,
        classification: this.search.classification,
        textbook: this.search.textbook,
        grade: this.search.grade,
        pageSize: this.search.pageSize,
        pageNumber: this.search.pageNumber,
        type: this.search.type
      }

      // 学段映射 - 使用 gradeLevel 字段名
      if (this.search.educationStage === 'primary') {
        params.gradeLevel = '小学'
      } else if (this.search.educationStage === 'junior') {
        params.gradeLevel = '初中'
      } else if (this.search.educationStage === 'senior') {
        params.gradeLevel = '高中'
      } else if (this.search.educationStage === 'other') {
        params.gradeLevel = '其他'
      }

      getApeTaskPage(params).then(res => {
        if(res.code == 1000) {
          this.tableData = res.data.records
          this.total = res.data.total
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    refresh() {
      this.search = {
        name: "",
        teacherName: "",
        major: "",
        state: 0,
        taskDescribe: "",
        classification: "",
        educationStage: "",
        textbook: "",
        grade: "",
        pageSize: 12,
        pageNumber: 1,
        type: 0,
      }
      this.showTextbookSelect = false
      this.showGradeSelect = false
      this.showClassificationNav = false
      this.query()
    },
    handleCurrentChange(val) {
      this.search.pageNumber = val
      this.query()
    },
    toTaskInfo(id) {
      this.$router.push("taskInfo?id=" + id)
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
    }
  },
  created() {

  },
  mounted() {
    this.getApeClassificationList()
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
    var name = this.$route.query.name
    if (name) {
      this.search.name = name
    }
    var major = this.$route.query.major
    if (major) {
      this.search.major = major
    }
    this.query()
  }
}
</script>

<style scoped>
@import url("../../assets/css/task/task.css");
</style>