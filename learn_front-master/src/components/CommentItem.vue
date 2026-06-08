<template>
  <div class="comment-wrapper">
    <!-- 主评论（第一层） -->
    <div class="comment-item">
      <div class="comment-avatar">
        <img :src="avatarUrl" @error="handleAvatarError">
      </div>
      <div class="comment-content-wrapper">
        <div class="comment-header">
          <span class="comment-author">{{ comment.userName || comment.createBy }}</span>
          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
        </div>
        <div class="comment-text">
          <span v-if="comment.replyTo" class="reply-to">@{{ comment.replyTo }} </span>
          {{ comment.content }}
        </div>
        <div class="comment-footer">
          <span class="comment-like" @click="$emit('like', comment)">
            <i class="el-icon-thumb" :class="{ 'liked': comment.isLiked }"></i>
            {{ comment.likeCount || 0 }}
          </span>
          <span class="comment-reply" @click="$emit('reply', comment)">
            <i class="el-icon-chat-line-round"></i> 回复
          </span>
        </div>
      </div>
    </div>

    <!-- 回复列表（第二层） -->
    <div v-if="hasReplies" class="replies-list">
      <div
          v-for="reply in allReplies"
          :key="reply.id"
          class="reply-item"
      >
        <div class="reply-avatar">
          <img :src="getUserAvatar(reply)" @error="handleAvatarError">
        </div>
        <div class="reply-body">
          <div class="reply-header">
            <span class="reply-author">{{ reply.userName || reply.createBy }}</span>
            <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
          </div>
          <div class="reply-text">
            <!-- 关键：显示 @ 被回复人 -->
            <span v-if="reply.replyTo" class="reply-to">@{{ reply.replyTo }} </span>
            {{ reply.content }}
          </div>
          <div class="reply-footer">
            <span class="reply-like" @click="$emit('like', reply)">
              <i class="el-icon-thumb" :class="{ 'liked': reply.isLiked }"></i>
              {{ reply.likeCount || 0 }}
            </span>
            <span class="reply-reply" @click="$emit('reply', reply)">
              回复
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommentItem',
  props: {
    comment: {
      type: Object,
      required: true
    }
  },
  computed: {
    hasReplies() {
      return this.comment.replies && this.comment.replies.length > 0
    },
    allReplies() {
      const result = []
      const flattenReplies = (replies) => {
        replies.forEach(reply => {
          result.push(reply)
          if (reply.replies && reply.replies.length > 0) {
            flattenReplies(reply.replies)
          }
        })
      }
      if (this.comment.replies) {
        flattenReplies(this.comment.replies)
      }
      result.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
      return result
    },
    avatarUrl() {
      // 兼容论坛评论的 userAvatar 和课程评论的 avatar
      const avatar = this.comment.userAvatar || this.comment.avatar
      if (avatar) {
        return this.$store.state.HOST + avatar
      }
      return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  methods: {
    handleAvatarError(e) {
      e.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    getUserAvatar(user) {
      // 兼容论坛评论的 userAvatar 和课程评论的 avatar
      const avatar = user.userAvatar || user.avatar
      if (avatar) {
        return this.$store.state.HOST + avatar
      }
      return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      if (timeStr.length > 16) {
        return timeStr.substring(0, 16)
      }
      return timeStr
    }
  }
}
</script>

<style scoped>
/* 评论包装器 */
.comment-wrapper {
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-wrapper:last-child {
  border-bottom: none;
}

/* 第一层 - 主评论样式 */
.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
}

.comment-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-content-wrapper {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: #20B486;
  cursor: pointer;
}

.comment-author:hover {
  text-decoration: underline;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  margin-bottom: 10px;
}

.reply-to {
  color: #20B486;
  font-weight: 500;
  margin-right: 4px;
}

.comment-footer {
  display: flex;
  gap: 20px;
}

.comment-like,
.comment-reply {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.comment-like:hover,
.comment-reply:hover {
  color: #20B486;
}

.comment-like i.liked {
  color: #ff4d4f;
}

/* 第二层 - 回复列表样式 */
.replies-list {
  margin-left: 52px;
  margin-bottom: 16px;
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 8px 0;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: 1px solid #eef2f6;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.reply-body {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.reply-author {
  font-size: 13px;
  font-weight: 600;
  color: #20B486;
  cursor: pointer;
}

.reply-author:hover {
  text-decoration: underline;
}

.reply-time {
  font-size: 11px;
  color: #999;
}

.reply-text {
  font-size: 13px;
  line-height: 1.5;
  color: #444;
  margin-bottom: 6px;
}

.reply-to {
  color: #20B486;
  font-weight: 500;
}

.reply-footer {
  display: flex;
  gap: 16px;
}

.reply-like,
.reply-reply {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.reply-like:hover,
.reply-reply:hover {
  color: #20B486;
}

.reply-like i.liked {
  color: #ff4d4f;
}
</style>