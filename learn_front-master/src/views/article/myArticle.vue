<template>
  <div class="article">
    <headerPage></headerPage>
    <div class="article-page-content">
      <div class="article-page-header-wrapper">
        <div class="article-page-title">笔记列表</div>
        <div class="article-page-btn">
          <el-button type="primary" size="small" icon="el-icon-edit" @click="toAddArticle">记笔记</el-button>
        </div>
      </div>
      <div class="article-page-list">
        <div @click="toArticleInfo(item.id)" v-for="(item,index) in tableData" :key="index" class="article-page-item">
          <div class="article-page-header">
            <div class="article-page-image"><img src="../../assets/image/index/Vector (1).png"></div>
            <div class="article-page-user">
              <img class="article-page-avatar" :src="getAvatarUrl(item)">
              <div class="article-page-username">
                <div>{{item.createBy}} - {{formatDate(item.createTime)}}</div>
                <div class="article-course-name">
                  <i class="el-icon-reading"></i> 课程：{{item.taskName || '未关联课程'}}
                </div>
              </div>
            </div>
          </div>
          <div class="article-page-title-content">
            <div class="article-page-title-text">{{item.title}}</div>
            <div class="article-page-font">{{item.articleDesc}}</div>
          </div>
        </div>
      </div>
      <div class="article-page-fenye">
        <el-pagination
            background
            :page-size="pageSize"
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
import {getApeArticlePage} from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"

export default {
  data() {
    return{
      user: {},
      title: "",
      pageSize: 10,
      pageNumber: 1,
      tableData: [],
      total: 0,
      taskId: "",      // 课程ID
      taskName: ""     // 课程名称
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    getAvatarUrl(item) {
      if (item.avatar) {
        return this.$store.state.HOST + item.avatar
      }
      return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    initParams() {
      // 获取路由参数中的课程ID
      this.taskId = this.$route.query.taskId || ""
      this.taskName = this.$route.query.taskName || ""
      console.log('课程ID:', this.taskId)
      console.log('课程名称:', this.taskName)
      // 重置分页
      this.pageNumber = 1
      this.query()
    },
    query() {
      var param = {
        state: 0,
        userId: this.user.id,
        pageSize: this.pageSize,
        pageNumber: this.pageNumber,
        type: 0,
      }
      // 如果有课程ID，则按课程ID筛选
      if (this.taskId) {
        param.taskId = this.taskId
      }
      getApeArticlePage(param).then(res => {
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
    handleCurrentChange(val) {
      this.pageNumber = val
      this.query()
    },
    toArticleInfo(id) {
      this.$router.push("/articleInfo?id="+id)
    },
    toAddArticle() {
      // 如果有课程ID，则传递给记笔记页面
      if (this.taskId) {
        this.$router.push("/addArticle?taskId=" + this.taskId + "&taskName=" + encodeURIComponent(this.taskName))
      } else {
        this.$router.push("/addArticle")
      }
    }
  },
  watch: {
    // 监听路由变化，当参数改变时重新加载数据
    '$route': {
      handler() {
        this.initParams()
      },
      immediate: false,
      deep: true
    }
  },
  mounted() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
    this.user = JSON.parse(window.localStorage.getItem("user_info"))
    this.initParams()
  }
}
</script>

<style scoped>
@import url("../../assets/css/article/article.css");

/* 标题行样式 - 笔记列表居中，按钮在右侧 */
.article-page-header-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  width: 70%;
  margin: 15px auto 0;
}

.article-page-title {
  font-size: 25px;
  font-weight: normal;
  color: #333;
  margin-left: 480px;
}

.article-page-btn {
  position: absolute;
  right: 0;
  top: 60%;
  transform: translateY(-20%);
}

/* 新增样式 */
.article-page-user {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.article-page-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
}

.article-page-username {
  flex: 1;
}

.article-page-username div:first-child {
  font-size: 14px;
  font-weight: 500;
  color: #20B486;
  margin-bottom: 4px;
}

.article-course-name {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-course-name i {
  font-size: 12px;
}

.article-page-title-content {
  margin-top: 12px;
  padding-left: 56px;
}

.article-page-title-text {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-page-font {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>