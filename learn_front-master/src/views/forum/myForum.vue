<template>
  <div class="my-forum">
    <headerPage></headerPage>
    <div class="my-forum-content">
      <div class="my-forum-header">
        <div class="header-title">
          <i class="el-icon-chat-dot-round"></i>
          我的帖子
        </div>
        <div class="header-desc">我发布的全部帖子</div>
      </div>

      <!-- 帖子列表 -->
      <div class="forum-list">
        <div v-if="tableData.length <= 0" class="empty-data">
          <el-empty description="还没有发布过帖子，快去发布吧！">
            <el-button type="primary" @click="toForum">去发布帖子</el-button>
          </el-empty>
        </div>
        <div v-else>
          <div v-for="(item,index) in tableData" :key="index" class="forum-card">
            <div class="card-header">
              <div class="card-title" @click="toForumInfo(item.id)">{{ item.name }}</div>
              <div class="card-actions">
                <el-button
                    type="danger"
                    size="mini"
                    icon="el-icon-delete"
                    circle
                    @click.stop="deleteForum(item.id, index)">
                </el-button>
              </div>
            </div>
            <div class="card-time">
              <i class="el-icon-time"></i> {{ item.createTime }}
            </div>
            <div class="card-content" @click="toForumInfo(item.id)">{{ item.content | truncate(100) }}</div>
            <div class="card-footer">
              <span class="card-stats">
                <i class="el-icon-thumb"></i> {{ item.likeCount || 0 }}
              </span>
              <span class="card-stats">
                <i class="el-icon-chat-dot-round"></i> {{ item.commentCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="my-forum-fenye" v-if="tableData.length > 0">
        <el-pagination
            background
            :page-size="pageSize"
            layout="total, prev, pager, next"
            @current-change="handleCurrentChange"
            :total="total">
        </el-pagination>
      </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import { getMyForumPage, removeMyForum } from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"

export default {
  components: {
    headerPage,
    bottomPage
  },
  filters: {
    truncate(value, length) {
      if (!value) return ''
      if (value.length <= length) return value
      return value.substring(0, length) + '...'
    }
  },
  data() {
    return {
      pageSize: 10,
      pageNumber: 1,
      tableData: [],
      total: 0
    }
  },
  methods: {
    toForum() {
      this.$router.push("/forum")
    },
    toForumInfo(id) {
      this.$router.push("/forumInfo?id=" + id)
    },
    // 删除帖子
    deleteForum(id, index) {
      this.$confirm('确定要删除这个帖子吗？删除后无法恢复！', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeMyForum({ id: id }).then(res => {
          if (res.code == 1000) {
            this.$message.success('删除成功')
            // 从列表中移除
            this.tableData.splice(index, 1)
            // 如果当前页没有数据了，往前翻一页
            if (this.tableData.length === 0 && this.pageNumber > 1) {
              this.pageNumber--
              this.query()
            } else {
              this.total--
            }
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
    query() {
      var param = {
        pageSize: this.pageSize,
        pageNumber: this.pageNumber
      }
      getMyForumPage(param).then(res => {
        if (res.code == 1000) {
          this.tableData = res.data.records
          this.total = res.data.total
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          })
        }
      })
    },
    handleCurrentChange(val) {
      this.pageNumber = val
      this.query()
    }
  },
  mounted() {
    this.query()
  }
}
</script>

<style scoped>
.my-forum {
  width: 100%;
  height: 100%;
}

.my-forum-content {
  width: 70%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 0;
}

.my-forum-header {
  text-align: center;
  margin-bottom: 40px;
}

.header-title {
  font-size: 28px;
  font-weight: bold;
  color: #20B486;
  margin-bottom: 10px;
}

.header-title i {
  margin-right: 10px;
}

.header-desc {
  font-size: 14px;
  color: #999;
}

.forum-list {
  min-height: 400px;
}

.empty-data {
  text-align: center;
  padding: 60px 0;
}

.forum-card {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  position: relative;
}

.forum-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: #20B486;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  flex-wrap: wrap;
  gap: 10px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  cursor: pointer;
  flex: 1;
}

.card-title:hover {
  color: #20B486;
}

.card-actions {
  flex-shrink: 0;
}

.card-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
}

.card-time i {
  margin-right: 4px;
}

.card-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  cursor: pointer;
}

.card-content:hover {
  color: #20B486;
}

.card-footer {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.card-stats {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.card-stats i {
  font-size: 14px;
}

.my-forum-fenye {
  margin-top: 30px;
  text-align: center;
}

.my-forum-fenye ::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #20B486;
}

/* 响应式 */
@media (max-width: 768px) {
  .my-forum-content {
    width: 95%;
    padding: 20px 0;
  }

  .header-title {
    font-size: 22px;
  }

  .forum-card {
    padding: 15px 18px;
  }

  .card-title {
    font-size: 16px;
  }
}
</style>