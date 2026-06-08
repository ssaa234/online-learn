<template>
  <div class="teacher">
    <headerPage></headerPage>
    <div class="teacher-page-content">
      <!-- 搜索区域 - K12风格 -->
      <div class="teacher-page-search">
        <div class="search-container">
          <div class="search-box">
            <i class="el-icon-search search-icon"></i>
            <el-input
                size="medium"
                v-model="searchData.userName"
                placeholder="搜索老师名字..."
                clearable
                @keyup.enter.native="searchPage()"
                class="search-input">
            </el-input>
            <el-button type="primary" size="medium" @click="searchPage()" class="search-btn">找老师</el-button>
          </div>
          <div class="search-tip">
            <i class="el-icon-info"></i> 共有 {{ total }} 位优秀老师
          </div>
        </div>
      </div>

      <!-- 教师列表 -->
      <div class="teacher-list">
        <div
            @click="toTeacherInfo(item.id)"
            v-for="(item,index) in tableData"
            :key="index"
            class="teacher-card">
          <div class="card-avatar">
            <img :src="getAvatarUrl(item.avatar)">
            <div class="card-badge" v-if="item.agree">
              {{ item.agree }}
            </div>
          </div>
          <div class="card-info">
            <div class="teacher-name">
              <span>{{ item.userName }}</span>
            </div>
            <div class="teacher-remark">
              {{ item.remark || '这位老师很认真，还没有留下介绍~' }}
            </div>
            <div class="teacher-stats">
              <span><i class="el-icon-collection"></i> {{ item.courseCount || 0 }}门课程</span>
              <span><i class="el-icon-user"></i> {{ item.studentCount || 0 }}名学生</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="tableData.length === 0 && !loading" class="empty-state">
        <i class="el-icon-notebook-2"></i>
        <p>暂无相关老师，试试其他关键词吧~</p>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
            background
            :page-size="searchData.pageSize"
            layout="prev, pager, next"
            @current-change="handleCurrentChange"
            :total="total"
            v-if="total > 0">
        </el-pagination>
      </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {getUserPage} from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"

export default {
  data() {
    return{
      searchData: {
        userName: "",
        userType: 1,
        pageSize: 12,
        pageNumber: 1
      },
      tableData: [],
      total: 0,
      loading: false
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    searchPage() {
      this.searchData.pageNumber = 1
      this.query()
    },
    query() {
      this.loading = true
      getUserPage(this.searchData).then(res => {
        this.loading = false
        if(res.code == 1000) {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      }).catch(() => {
        this.loading = false
      })
    },
    refresh() {
      this.searchData = {
        userName: "",
        userType: 1,
        pageSize: 12,
        pageNumber: 1
      }
      this.query()
    },
    handleCurrentChange(val) {
      this.searchData.pageNumber = val
      this.query()
    },
    toTeacherInfo(id) {
      this.$router.push("/teacherInfo?id=" + id)
    },
    getAvatarUrl(avatar) {
      // 如果没有头像，返回空字符串（不显示图片）
      if (!avatar) {
        return ''
      }
      // 如果是完整URL，直接返回
      if (avatar.startsWith('http')) {
        return avatar
      }
      // 否则拼接后端地址
      return this.$store.state.HOST + avatar
    }
  },
  mounted() {
    this.query()
  }
}
</script>

<style scoped>
@import url("../../assets/css/teacher/teacher.css");
</style>