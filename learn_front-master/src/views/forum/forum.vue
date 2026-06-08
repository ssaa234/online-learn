<template>
  <div class="notice">
    <headerPage></headerPage>
    <div class="notice-page-content">
      <div class="notice-page-header">
        <div class="notice-page-title">讨论列表</div>
        <div class="notice-page-btn">
          <el-button type="primary" size="small" icon="el-icon-edit" @click="openPostDialog">发布帖子</el-button>
        </div>
      </div>

      <!-- 帖子列表 - 卡片式布局 -->
      <div class="notice-page-list">
        <div v-for="(item,index) in tableData" :key="index" class="notice-page-card" @click="toForumInfo(item.id)">
          <!-- 用户信息行 -->
          <div class="card-user">
            <img class="user-avatar" :src="$store.state.HOST + item.avatar" @error="handleAvatarError">
            <div class="user-info">
              <span class="user-name">{{item.createBy || '匿名用户'}}</span>
              <span class="user-time">{{item.createTime}}</span>
            </div>
          </div>
          <!-- 帖子内容 -->
          <div class="card-content">
            <div class="card-title">{{item.name}}</div>
            <div class="card-desc">{{item.content | truncate(100)}}</div>
          </div>
          <!-- 帖子统计 -->
          <div class="card-stats">
            <span class="stat-item">
              <i class="el-icon-thumb"></i> {{item.likeCount || 0}}
            </span>
            <span class="stat-item">
              <i class="el-icon-chat-dot-round"></i> {{item.commentCount || 0}}
            </span>
          </div>
        </div>
      </div>

      <div class="notice-page-fenye">
        <el-pagination
            background
            :page-size="pageSize"
            layout="prev, pager, next"
            @current-change="handleCurrentChange"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 发布帖子对话框 -->
    <el-dialog
        title="发布新帖子"
        :visible.sync="postDialogVisible"
        width="500px"
        :before-close="handleClose">
      <el-form :model="postForm" :rules="postRules" ref="postFormRef" label-width="80px">
        <el-form-item label="标题" prop="name">
          <el-input
              v-model="postForm.name"
              placeholder="请输入帖子标题"
              maxlength="100"
              show-word-limit>
          </el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
              type="textarea"
              v-model="postForm.content"
              placeholder="请输入帖子内容"
              :rows="8"
              maxlength="500"
              show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="postDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitPost" :loading="submitting">发 布</el-button>
      </span>
    </el-dialog>

    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {getApeForumPage, saveApeForum} from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
export default {
  data() {
    return{
      pageSize: 10,
      pageNumber: 1,
      tableData: [],
      total: 0,
      notice: {},
      // 发布帖子相关
      postDialogVisible: false,
      submitting: false,
      postForm: {
        name: '',
        content: ''
      },
      postRules: {
        name: [
          { required: true, message: '请输入帖子标题', trigger: 'blur' },
          { min: 1, max: 100, message: '标题长度在1-100个字符之间', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入帖子内容', trigger: 'blur' },
          { min: 1, max: 500, message: '内容长度在1-500个字符之间', trigger: 'blur' }
        ]
      }
    }
  },
  filters: {
    truncate(value, length) {
      if (!value) return ''
      if (value.length <= length) return value
      return value.substring(0, length) + '...'
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    handleAvatarError(e) {
      e.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    toForumInfo(id) {
      this.$router.push("forumInfo?id="+id)
    },
    query() {
      var param = {
        pageSize: this.pageSize,
        pageNumber: this.pageNumber,
      }
      getApeForumPage(param).then(res => {
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
    openPostDialog() {
      this.postForm = { name: '', content: '' }
      this.postDialogVisible = true
    },
    handleClose() {
      this.postDialogVisible = false
      this.$refs.postFormRef?.resetFields()
    },
    submitPost() {
      this.$refs.postFormRef.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            const params = {
              name: this.postForm.name,
              content: this.postForm.content
            }
            const res = await saveApeForum(params)
            if (res.code == 1000) {
              this.$message({
                message: '发布成功',
                type: 'success',
                duration: 1500
              })
              this.postDialogVisible = false
              this.$refs.postFormRef.resetFields()
              this.pageNumber = 1
              this.query()
            } else {
              this.$message.error(res.message || '发布失败')
            }
          } catch (error) {
            this.$message.error('发布失败，请稍后重试')
          } finally {
            this.submitting = false
          }
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
    this.query()
  }
}
</script>

<style scoped>
@import url("../../assets/css/notice/notice.css");

.notice-page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 70%;
  margin-top: 30px;
  position: relative;
}

.notice-page-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  margin-top: 0;
  font-size: 25px;
  white-space: nowrap;
}

.notice-page-btn {
  margin-left: auto;
  flex-shrink: 0;
}

/* 帖子列表样式 */
.notice-page-list {
  width: 70%;
}

/* 卡片样式 */
.notice-page-card {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
}

.notice-page-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: #20B486;
}

/* 用户信息行 */
.card-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e8e8e8;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #20B486;
}

.user-time {
  font-size: 12px;
  color: #999;
}

/* 帖子内容 */
.card-content {
  margin-bottom: 16px;
  padding-left: 0;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  margin-bottom: 8px;
}

.card-desc {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 帖子统计 */
.card-stats {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
  transition: color 0.3s;
}

.stat-item i {
  font-size: 16px;
}

.stat-item:hover {
  color: #20B486;
}

/* 分页样式 */
.notice-page-fenye {
  width: 70%;
  margin-top: 20px;
  margin-bottom: 40px;
  text-align: center;
}
</style>