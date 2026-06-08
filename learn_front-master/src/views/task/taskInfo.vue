<template>
  <div class="taskInfo">
    <headerPage></headerPage>
    <div class="taskInfo-content">
      <div class="taskInfo-name">
        <div class="taskInfo-desc">
          <div class="taskInfo-title">{{task.name}}
            <el-button size="mini" v-if="flag" type="warning" @click="removeFavor" icon="el-icon-star-off" circle></el-button>
            <el-button size="mini" v-if="!flag" type="success" @click="addFavor" icon="el-icon-star-on" circle></el-button>
          </div>
          <div class="taskInfo-title1">
            <el-tooltip class="item" effect="dark" :content="task.taskDescribe" placement="bottom">
              <i class="el-icon-info"></i>
            </el-tooltip>
            {{task.taskDescribe}}
          </div>
        </div>
        <div class="taskInfo-list">
          <div class="taskInfo-image">
            <div class="taskInfo-fengmian">
              <video v-if="select" ref="videoPlayer" id="videoPlayer" :src="selectSrc" class="video-js" style="width:100%;height:100%"></video>
              <img v-if="!select" style="width:100%;height:100%" :src="$store.state.HOST + task.image">
            </div>
            <div class="taskInfo-btns">
              <div @click="addTaskStudent" title="报名课程" class="taskInfo-icon">
                <i class="el-icon-document-checked"></i>
              </div>
              <div @click="toQuestion(task.id)" title="课程答疑" class="taskInfo-icon">
                <i class="el-icon-question"></i>
              </div>
              <div @click="toTeacher(task.teacherId)" title="课程讲师" class="taskInfo-icon">
                <i class="el-icon-link"></i>
              </div>
              <div @click="toArticle" title="课程笔记" class="taskInfo-icon">
                <i class="el-icon-notebook-1"></i>
              </div>
              <div @click="toAssign()" title="章节作业" class="taskInfo-icon">
                <i class="el-icon-news"></i>
              </div>
              <div @click="downloadfile" title="下载课件" class="taskInfo-icon">
                <i class="el-icon-download"></i>
              </div>
              <div @click="toLearning" title="学习进度" class="taskInfo-icon">
                <i class="el-icon-data-line"></i>
              </div>
            </div>
          </div>
          <div class="taskInfo-video">
            <div @click="changeChapter(index)" class="taskInfo-video-item" v-for="(item,index) in chapter" :key="index">
              <div :title="item.name" class="taskInfo-video-item1">{{item.name}}</div>
              <div class="taskInfo-video-item2"><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论输入区域 -->
    <div class="taskInfo-bottom">
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
            <el-button @click="saveApeTaskComment" size="small" type="primary" plain>发布评论</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 - 树状结构 -->
    <div class="taskInfo-bottom">
      <div class="comment-section">
        <div class="comment-title">
          <i class="el-icon-chat-dot-round"></i> 全部评论 ({{totalCommentCount}})
        </div>

        <div v-if="commentList.length <= 0" class="empty-comment">
          <el-empty description="还没有评论，快来发表你的看法吧！"></el-empty>
        </div>

        <div v-if="commentList.length > 0" class="comment-list">
          <CommentItem
              v-for="item in commentList"
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
import {
  saveApeTaskFavor,
  removeApeTaskFavor,
  getApeTaskFavorById,
  saveApeChapterVideo,
  getApeHomeworkByChapterId,
  getApeTaskById,
  saveApeTaskStudent,
  getTaskStudent,
  getApeChapterByTaskId,
  saveApeTaskComment,
  getApeTaskCommentListByTaskId,
  likeApeTaskComment
} from "../../api/api"
import videojs from 'video.js'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
import CommentItem from '../../components/CommentItem.vue'

export default {
  data() {
    return{
      user: {},
      taskId: "",
      state: 2,
      task: {},
      chapter: [],
      selectChapter: "",
      selectSrc: "",
      select: false,
      commentList: [],
      content: "",
      player: null,
      options: {
        autoplay: true,
        controls: true,
        sources: []
      },
      favor: {},
      flag: false,
      replyParentId: null,
      replyUserName: "",
      replyToUserId: null
    }
  },
  components: {
    headerPage,
    bottomPage,
    CommentItem
  },
  computed: {
    totalCommentCount() {
      let count = 0
      this.commentList.forEach(comment => {
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
      console.log('点赞评论，评论ID:', item.id)
      const params = {
        commentId: item.id,
        type: 2
      }
      likeApeTaskComment(params).then(res => {
        console.log('点赞响应:', res)
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
      }).catch(err => {
        console.log('点赞错误:', err)
        this.$message.error('操作失败，请稍后重试')
      })
    },
    replyToUser(item) {
      this.replyParentId = item.id
      this.replyUserName = item.createBy
      this.replyToUserId = item.userId
      this.content = ''
      console.log('回复用户:', this.replyUserName, '评论ID:', this.replyParentId)
      this.$nextTick(() => {
        const textarea = document.querySelector('.comment-textarea textarea')
        if (textarea) textarea.focus()
      })
    },
    cancelReply() {
      this.replyParentId = null
      this.replyUserName = ""
      this.replyToUserId = null
      this.content = ""
    },
    removeFavor() {
      removeApeTaskFavor({ids:this.favor.id}).then(res => {
        if (res.code == 1000) {
          this.$message({
            type: 'success',
            message: '取消收藏成功'
          });
          this.getApeTaskFavorById()
        }
      })
    },
    addFavor() {
      var param = {
        taskId: this.taskId,
        userId: this.user.id
      }
      saveApeTaskFavor(param).then(res => {
        if (res.code == 1000) {
          this.$message({
            type: 'success',
            message: '收藏成功'
          });
          this.getApeTaskFavorById()
        }
      })
    },
    getApeTaskFavorById() {
      getApeTaskFavorById({taskId: this.taskId,userId:this.user.id}).then(res => {
        if (res.code == 1000) {
          this.favor = res.data
          this.flag = true
        } else {
          this.favor = {}
          this.flag = false
        }
      })
    },
    getApeTaskCommentListByTaskId() {
      getApeTaskCommentListByTaskId({id:this.taskId}).then(res => {
        if (res.code == 1000) {
          this.commentList = this.buildCommentTree(res.data)
        }
      })
    },
    buildCommentTree(comments) {
      const commentMap = new Map()
      const result = []

      // 先建立所有评论的映射，保留所有字段
      comments.forEach(comment => {
        comment.replies = []
        // 确保 replyTo 字段存在
        console.log('原始评论 - ID:', comment.id, 'createBy:', comment.createBy, 'replyTo:', comment.replyTo)
        commentMap.set(comment.id, comment)
      })

      // 构建树形结构
      comments.forEach(comment => {
        if (comment.parentId && commentMap.has(comment.parentId)) {
          const parent = commentMap.get(comment.parentId)
          // 查找最顶层的父评论
          let topParent = parent
          while (topParent.parentId && commentMap.has(topParent.parentId)) {
            topParent = commentMap.get(topParent.parentId)
          }
          topParent.replies.push(comment)
        } else {
          result.push(comment)
        }
      })

      // 去重
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

      console.log('构建后的树形结构:', result)
      return result
    },
    toQuestion(id) {
      if (this.state != 0) {
        this.$message({
          type: 'warning',
          message: '报名课程通过后才能进行答疑'
        });
        return
      }
      this.$router.push("/question?id=" + id)
    },
    saveApeTaskComment() {
      if (this.state != 0) {
        this.$message({
          type: 'warning',
          message: '报名课程通过后才能发表评论'
        });
        return
      }
      if (!this.content.trim()) {
        this.$message({
          type: 'warning',
          message: '请输入评论内容'
        });
        return
      }

      var param = {
        content: this.content,
        taskId: this.taskId
      }

      // 如果是回复，添加 parentId 和 replyTo
      if (this.replyParentId) {
        param.parentId = this.replyParentId
        param.replyTo = this.replyUserName  // 关键：传递被回复的用户名
      }

      console.log('提交评论参数:', param)

      saveApeTaskComment(param).then(res => {
        if (res.code == 1000) {
          this.$message({
            type: 'success',
            message: '评论成功'
          });
          this.content = ""
          this.cancelReply()
          this.getApeTaskCommentListByTaskId()
        }
      })
    },
    toTeacher(id) {
      this.$router.push("/teacherInfo?id=" + id)
    },
    toArticle() {
      this.$router.push("/myArticle?taskId=" + this.task.id + "&taskName=" + encodeURIComponent(this.task.name))
    },
    toLearning() {
      if (this.state != 0) {
        this.$message({
          type: 'warning',
          message: '请先报名课程'
        });
        return
      }
      this.$router.push("/rateOfLearning?id="+this.task.id)
    },
    downloadfile() {
      if (this.state != 0) {
        this.$message({
          type: 'warning',
          message: '报名课程通过后才能下载课件'
        });
        return
      }
      if (this.selectChapter == "") {
        this.$message({
          type: 'warning',
          message: '请先选择章节再下载课件'
        });
        return
      }
      if (this.selectChapter.courseware) {
        window.location.href = this.$store.state.HOST + this.selectChapter.courseware
      } else {
        this.$message({
          type: 'warning',
          message: '该章节暂无课件'
        });
        return
      }
    },
    changeChapter(index) {
      if (this.state != 0) {
        this.$message({
          type: 'warning',
          message: '报名课程通过后才能观看视频'
        });
        return
      }
      this.select = false
      this.selectChapter = this.chapter[index]
      var that = this
      this.selectSrc = this.$store.state.HOST + this.selectChapter.video
      this.select = true
      if (this.player == null) {
        setTimeout(function() {
          that.player = videojs("videoPlayer", that.options, function onPlayerReady() {})
          that.player.on("ended", () => {
            var param = {
              chapterId: that.selectChapter.id,
            }
            saveApeChapterVideo(param)
          });
        },2000)
      }
    },
    addTaskStudent() {
      if (this.state == 0) {
        this.$message({
          type: 'success',
          message: '报名已通过无需重复操作'
        });
      } else if(this.state == 1) {
        this.$message({
          type: 'success',
          message: '已存在报名记录，请耐心等待教师通过！'
        });
      } else {
        var param = {
          taskId: this.taskId
        }
        saveApeTaskStudent(param).then(res => {
          if (res.code == 1000) {
            this.$message({
              type: 'success',
              message: '报名成功，请等待教师通过!'
            });
            getTaskStudent({id:this.taskId}).then(res => {
              if (res.code == 1000) {
                this.state = res.data
              }
            })
          }
        })
      }
    },
    toAssign() {
      if (this.state != 0) {
        this.$message({
          type: 'warning',
          message: '报名课程通过后才能完成作业'
        });
        return
      }
      if (this.selectChapter == "") {
        this.$message({
          type: 'warning',
          message: '请先选择课程章节!'
        });
        return
      }
      getApeHomeworkByChapterId({id:this.selectChapter.id}).then(res => {
        if (res.code == 1000) {
          this.$router.push("/assignInfo?id="+ this.selectChapter.id)
        } else {
          this.$message({
            type: 'warning',
            message: '该章节暂未发布作业!'
          });
        }
      })
    }
  },
  mounted() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
    this.user = JSON.parse(window.localStorage.getItem("user_info"))
    var that = this
    this.taskId = this.$route.query.id
    getApeTaskById({id:this.taskId}).then(res => {
      if (res.code == 1000) {
        this.task = res.data
      }
    })
    getTaskStudent({id:this.taskId}).then(res => {
      if (res.code == 1000) {
        this.state = res.data
      }
    })
    getApeChapterByTaskId({id: this.taskId}).then(res => {
      if (res.code == 1000) {
        this.chapter = res.data
      }
    })
    this.getApeTaskCommentListByTaskId()
    this.getApeTaskFavorById()
  },
  beforeDestroy() {
    if (this.player) {
      this.player.dispose()
    }
  }
}
</script>

<style scoped>
@import url("../../assets/css/task/taskInfo.css");

/* 评论区域样式 */
.comment-section {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  width: 100%;
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

/* 树状评论样式 */
.comment-wrapper {
  margin-bottom: 8px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 16px 0;
  position: relative;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item.has-child {
  border-bottom: 1px solid #f0f0f0;
}

.comment-line {
  position: absolute;
  left: -20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #20B486;
  opacity: 0.3;
}

.comment-avatar img {
  width: 42px;
  height: 42px;
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
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: #20B486;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #444;
  margin-bottom: 10px;
  word-break: break-word;
  background: #f8f9fa;
  padding: 12px 15px;
  border-radius: 12px;
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

/* 子评论容器 */
.comment-children {
  margin-top: 0;
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
    padding: 15px;
  }

  .comment-avatar img {
    width: 32px;
    height: 32px;
  }

  .comment-text {
    font-size: 13px;
    padding: 8px 12px;
  }

  .comment-line {
    left: -15px;
  }
}
</style>