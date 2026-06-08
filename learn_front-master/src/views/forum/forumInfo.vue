<template>
  <div class="articleInfo">
    <headerPage></headerPage>

    <!-- 帖子详情 -->
    <div class="articleInfo-content">
      <div class="post-container">
        <div class="post-header">
          <div class="post-author">
            <img class="author-avatar" :src="$store.state.HOST + forum.avatar" @error="handleAvatarError">
            <div class="author-info">
              <span class="author-name">{{forum.createBy}}</span>
              <span class="post-time">
                <i class="el-icon-time"></i> {{formatTime(forum.createTime)}}
              </span>
            </div>
          </div>
          <div class="post-title">{{forum.name}}</div>
        </div>

        <div class="post-content">
          <div class="content-text">{{forum.content}}</div>
        </div>

        <div class="post-actions">
          <div class="action-item" @click="likePost">
            <i class="el-icon-thumb" :class="{ 'liked': isLiked }"></i>
            <span>{{likeCount}}</span>
          </div>
          <div class="action-item">
            <i class="el-icon-chat-dot-round"></i>
            <span>{{totalCommentCount}} 回复</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论输入区域 -->
    <div class="articleInfo-bottom">
      <div class="comment-section">
        <div class="comment-title">
          <i class="el-icon-edit"></i> 发表评论
        </div>
        <div class="comment-input">
          <div v-if="replyUserName" class="reply-tip">
            正在回复 @{{replyUserName}}
            <span @click="cancelReply" class="cancel-reply">取消</span>
          </div>
          <el-input
              type="textarea"
              resize="none"
              rows="6"
              v-model="content"
              :placeholder="replyUserName ? `回复 ${replyUserName}...` : '写下你的评论...'"
              class="comment-textarea">
          </el-input>
          <div class="comment-submit">
            <el-button @click="saveApeForumItem" size="small" type="primary" plain>发布评论</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 - 树状结构（完全参照课程评论） -->
    <div class="articleInfo-bottom">
      <div class="comment-section">
        <div class="comment-title">
          <i class="el-icon-chat-dot-round"></i> 全部评论 ({{totalCommentCount}})
        </div>

        <div v-if="forumList.length <= 0" class="empty-comment">
          <el-empty description="还没有评论，快来发表你的看法吧！"></el-empty>
        </div>

        <div v-if="forumList.length > 0" class="comment-list">
          <CommentItem
              v-for="item in forumList"
              :key="item.id"
              :comment="item"
              :level="0"
              @reply="replyToUser"
              @like="likeComment" />
        </div>
      </div>
    </div>

    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {getApeForumById, getApeForumItemList, saveApeForumItem, likeApeForum} from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
import CommentItem from '../../components/CommentItem.vue'

export default {
  components: {
    headerPage,
    bottomPage,
    CommentItem
  },
  data() {
    return{
      id: "",
      user: {},
      forum: {},
      content: "",
      forumList: [],
      likeCount: 0,
      isLiked: false,
      replyParentId: null,
      replyUserName: ""
    }
  },
  computed: {
    totalCommentCount() {
      let count = 0
      this.forumList.forEach(comment => {
        count += 1
        if (comment.replies && comment.replies.length > 0) {
          count += comment.replies.length
        }
      })
      return count
    }
  },
  methods: {
    handleAvatarError(e) {
      e.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    likeComment(item) {
      const params = { targetId: item.id, type: 2 }
      likeApeForum(params).then(res => {
        if (res.code == 1000) {
          item.isLiked = !item.isLiked
          item.likeCount = res.data
          this.$forceUpdate()
          this.$message({
            message: item.isLiked ? '点赞成功' : '取消点赞',
            type: 'success',
            duration: 1000
          })
        } else {
          this.$message.error(res.message || '操作失败')
        }
      }).catch(() => {
        this.$message.error('操作失败，请稍后重试')
      })
    },
    replyToUser(item) {
      this.replyParentId = item.id
      this.replyUserName = item.userName || item.createBy
      this.content = ''
      this.$nextTick(() => {
        const textarea = document.querySelector('.comment-textarea textarea')
        if (textarea) textarea.focus()
      })
    },
    cancelReply() {
      this.replyParentId = null
      this.replyUserName = ""
      this.content = ""
    },
    likePost() {
      const params = { targetId: this.id, type: 1 }
      likeApeForum(params).then(res => {
        if (res.code == 1000) {
          this.isLiked = !this.isLiked
          this.likeCount = res.data || (this.isLiked ? this.likeCount + 1 : this.likeCount - 1)
          this.$message({
            message: this.isLiked ? '点赞成功' : '取消点赞',
            type: 'success',
            duration: 1000
          })
        } else {
          this.$message.error(res.message)
        }
      })
    },
    saveApeForumItem() {
      if (!this.content.trim()) {
        this.$message({ type: 'warning', message: '请输入评论内容' })
        return
      }

      var param = {
        forumId: this.id,
        content: this.content
      }

      if (this.replyParentId) {
        param.parentId = this.replyParentId
        param.replyTo = this.replyUserName
      }

      saveApeForumItem(param).then(res => {
        if (res.code == 1000) {
          this.$message({ type: 'success', message: '发布成功!' })
          this.content = ""
          this.cancelReply()
          this.getApeForumItemList()
        } else {
          this.$message.error(res.message)
        }
      }).catch(() => {
        this.$message.error('发布失败，请稍后重试')
      })
    },
    getApeForumById() {
      getApeForumById({id: this.id}).then(res => {
        if (res.code == 1000) {
          this.forum = res.data
          this.likeCount = res.data.likeCount || 0
          this.isLiked = res.data.isLiked || false
        }
      })
    },
    getApeForumItemList() {
      getApeForumItemList({forumId: this.id}).then(res => {
        if (res.code == 1000) {
          this.forumList = this.buildCommentTree(res.data || [])
        }
      })
    },
    buildCommentTree(comments) {
      const commentMap = new Map()
      const result = []

      comments.forEach(comment => {
        comment.replies = []
        commentMap.set(comment.id, comment)
      })

      comments.forEach(comment => {
        if (comment.parentId && commentMap.has(comment.parentId)) {
          const parent = commentMap.get(comment.parentId)
          let topParent = parent
          while (topParent.parentId && commentMap.has(topParent.parentId)) {
            topParent = commentMap.get(topParent.parentId)
          }
          topParent.replies.push(comment)
        } else {
          result.push(comment)
        }
      })

      result.forEach(comment => {
        if (comment.replies && comment.replies.length > 0) {
          const uniqueReplies = []
          const replyIds = new Set()
          comment.replies.forEach(reply => {
            if (!replyIds.has(reply.id)) {
              replyIds.add(reply.id)
              uniqueReplies.push(reply)
            }
          })
          comment.replies = uniqueReplies
          comment.replies.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
        }
      })

      result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      return result
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      if (timeStr.length > 16) {
        return timeStr.substring(0, 16)
      }
      return timeStr
    }
  },
  mounted() {
    window.scrollTo({ top: 0, behavior: 'smooth' })
    this.id = this.$route.query.id
    this.getApeForumById()
    this.getApeForumItemList()
  }
}
</script>

<style scoped>
@import url("../../assets/css/article/articleInfo.css");

/* 帖子容器样式 */
.post-container {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin: 30px auto;
  width: 70%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.post-header {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
  margin-bottom: 20px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #20B486;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-time i {
  margin-right: 4px;
}

.post-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
}

.post-content {
  margin-bottom: 24px;
  min-height: 150px;
}

.content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
  white-space: pre-wrap;
}

.post-actions {
  display: flex;
  gap: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
}

.action-item:hover {
  color: #20B486;
}

.action-item i {
  font-size: 18px;
}

.action-item i.liked {
  color: #ff4d4f;
}

/* 评论区域样式 - 完全参照课程评论 */
.comment-section {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  width: 70%;
  margin-left: auto;
  margin-right: auto;
}

.comment-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.comment-title i {
  margin-right: 8px;
  color: #20B486;
}

.comment-input {
  margin-bottom: 0;
}

.comment-textarea >>> .el-textarea__inner {
  min-height: 120px;
  font-size: 14px;
  line-height: 1.6;
  border-radius: 12px;
  padding: 12px 15px;
}

.comment-submit {
  margin-top: 12px;
  text-align: right;
}

.empty-comment {
  padding: 40px 0;
}

.comment-list {
  max-height: 600px;
  overflow-y: auto;
}

.reply-tip {
  background: #e8f5e9;
  padding: 8px 12px;
  border-radius: 8px;
  margin-bottom: 10px;
  font-size: 13px;
  color: #20B486;
}

.cancel-reply {
  color: #999;
  cursor: pointer;
  margin-left: 10px;
}

.cancel-reply:hover {
  color: #ff4444;
}

/* 滚动条样式 */
.comment-list::-webkit-scrollbar {
  width: 6px;
}

.comment-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.comment-list::-webkit-scrollbar-thumb {
  background: #20B486;
  border-radius: 3px;
}

/* 响应式 */
@media (max-width: 768px) {
  .comment-section {
    width: 95%;
    padding: 15px;
  }
}
</style>