<template>
  <div class="article">
    <headerPage></headerPage>
    <div class="article-page-content">
      <div class="article-page-search">
        <div style="width:30%;font-family:'黑体';font-family:'黑体';display:flex;align-items:center;margin-top:15px;margin-left:10px;margin-bottom:10px">
          <div>笔记名称：</div>
          <div>
            <el-input size="mini" v-model="title" placeholder="请输入笔记名称："></el-input>
          </div>
        </div>
        <div style="width:30%;font-family:'黑体';font-family:'黑体';display:flex;align-items:center;margin-top:15px;margin-left:10px;margin-bottom:10px">
          <div>课程名称：</div>
          <div>
            <el-input size="mini" v-model="taskName" placeholder="请输入课程名称："></el-input>
          </div>
        </div>
        <div class="article-page-btns">
          <el-button icon="el-icon-search" size="mini" type="primary" plain @click="searchPage()">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" type="warning" plain @click="refresh()">重置</el-button>
        </div>
      </div>
      <div class="article-page-title">
        笔记列表
      </div>
      <div class="article-page-list">
        <div @click="toArticleInfo(item.id)" v-for="(item,index) in tableData" :key="index" class="article-page-item">
          <div class="article-page-image"><img src="../../assets/image/index/Vector (1).png"></div>
          <div class="article-page-user">
            <img class="article-page-avatar" :src="$store.state.HOST + item.avatar">
            <div class="article-page-username">
              <div>{{item.createBy}} - {{item.createTime}}</div>
              <div>{{item.title}}</div>
            </div>
          </div>
          <div class="article-page-font">
            {{item.articleDesc}}
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
      title: "",
      taskName: "",
      pageSize: 10,
      pageNumber: 1,
      tableData: [],
      total: 0,
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    searchPage() {
      this.pageNumber = 1
      this.query()
    },
    query() {
      var param = {
        state: 0,
        title: this.title,
        taskName: this.taskName,
        pageSize: this.pageSize,
        pageNumber: this.pageNumber,
        type: 0,
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
    refresh() {
      this.title = ""
      this.pageNumber = 1
      this.query()
    },
    handleCurrentChange(val) {
      this.pageNumber = val
      this.query()
    },
    toArticleInfo(id) {
      this.$router.push("/articleInfo?id="+id)
    }
  },
  created() {

  },
  mounted() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
    var name = this.$route.query.name
    if (name) {
      this.taskName = name
    }
    this.query()
  }
}
</script>

<style scoped>
@import url("../../assets/css/article/article.css");
</style>