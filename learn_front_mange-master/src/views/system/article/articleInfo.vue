<template>
  <div class="articleInfo">
    <div class="articleInfo-content">
      <div class="articleInfo-title">
        <div>
          <div class="articleInfo-title1">{{article.title}}</div>
          <div class="articleInfo-title2">{{article.createBy}} - {{article.createTime}}</div>
        </div>
      </div>
      <div class="articleInfo-desc">
        <div class="articleInfo-desc1">
          <span style="font-weight:bold">摘要：</span>{{article.articleDesc}}
        </div>
        <div class="articleInfo-desc2">
          <span style="font-weight:bold">正文：</span>
          <div v-html="article.content">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getApeArticleById, removeApeArticle } from '../../../api/api'

export default {
  data() {
    return{
      user: {},
      articleId: "",
      article: {}
    }
  },
  methods: {
    // 删除笔记
    deleteArticle() {
      this.$confirm('确定要删除这篇笔记吗？删除后无法恢复！', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeApeArticle({ids: this.articleId}).then(res => {
          if (res.code == 1000) {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            // 延迟后返回笔记列表页
            setTimeout(() => {
              this.$router.push("/article")
            }, 1000)
          } else {
            this.$message.error(res.message || '删除失败')
          }
        }).catch(() => {
          this.$message.error('删除失败，请稍后重试')
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    editArticle(id) {
      this.$router.push("/editArticle?id=" + id)
    },
    getApeArticleById() {
      getApeArticleById({id: this.articleId}).then(res => {
        if(res.code == 1000) {
          this.article = res.data
        }
      })
    }
  },
  mounted() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
    var articleId = this.$route.query.id
    if (articleId) {
      this.articleId = articleId
    }
    this.getApeArticleById()
    this.user = JSON.parse(window.localStorage.getItem("user_info"))
  }
}
</script>

<style scoped>
@import url("../../../assets/css/articleInfo.css");
::v-deep img {
  max-width: 100% !important;
}
</style>