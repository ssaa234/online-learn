<template>
  <div class="notice">
    <headerPage></headerPage>
    <div class="notice-page-content">
      <!-- 当前公告详情卡片 -->
      <div class="notice-detail-card" v-if="notice.title">
        <div class="detail-header">
          <div class="detail-icon">
            <i class="el-icon-megaphone"></i>
          </div>
          <div class="detail-title">{{ notice.title }}</div>
          <div class="detail-time">
            <i class="el-icon-time"></i> {{ formatDate(notice.createTime) }}
          </div>
        </div>
        <div class="detail-content" v-html="notice.content"></div>
      </div>
      <div class="notice-detail-card empty-card" v-else>
        <div class="empty-tip">
          <i class="el-icon-reading"></i>
          <p>点击左侧公告查看详情</p>
        </div>
      </div>

      <!-- 公告列表标题 -->
      <div class="notice-list-header">
        <div class="header-line"></div>
        <div class="header-title">
          <i class="el-icon-notification"></i>
          公告列表
        </div>
        <div class="header-line"></div>
      </div>

      <!-- 公告列表 -->
      <div class="notice-list">
        <div
            v-for="(item,index) in tableData"
            :key="index"
            class="notice-item"
            :class="{ active: notice.id === item.id }"
            @click="getNoticeInfo(index)">
          <div class="item-badge">{{ index + 1 }}</div>
          <div class="item-content">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-meta">
              <span class="item-time">
                <i class="el-icon-time"></i> {{ formatDate(item.createTime) }}
              </span>
              <span class="item-author">
                <i class="el-icon-user"></i> {{ item.createBy || '系统' }}
              </span>
            </div>
          </div>
          <div class="item-arrow">
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="notice-page-fenye">
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
import { getAccountPage } from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
export default {
  data() {
    return {
      pageSize: 10,
      pageNumber: 1,
      tableData: [],
      total: 0,
      notice: {}
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
      // 检查日期是否有效
      if (isNaN(date.getTime())) return dateStr
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    getNoticeInfo(index) {
      this.notice = this.tableData[index]
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    query() {
      var param = {
        pageSize: this.pageSize,
        pageNumber: this.pageNumber,
      }
      getAccountPage(param).then(res => {
        if (res.code == 1000) {
          this.tableData = res.data.records
          this.total = res.data.total
          // 默认选中第一条（最新的一条，因为后端已按创建时间倒序）
          if (this.tableData.length > 0 && !this.notice.title) {
            this.notice = this.tableData[0]
          }
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
@import url("../../assets/css/notice/notice.css");

.notice {
  width: 100%;
  min-height: 100vh;
}

.notice-page-content {
  width: 80%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 0;
}

/* 公告详情卡片 */
.notice-detail-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 40px;
  transition: all 0.3s ease;
  width: 90%;
}

.detail-header {
  background: linear-gradient(135deg, #20B486 0%, #1a9a6e 100%);
  padding: 24px 32px;
  color: white;
}

.detail-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.detail-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
  line-height: 1.4;
}

.detail-time {
  font-size: 13px;
  opacity: 0.85;
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-content {
  padding: 32px;
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  min-height: 200px;
}

.detail-content img {
  max-width: 100%;
  border-radius: 8px;
}

.empty-card {
  background: white;
  min-height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-tip {
  text-align: center;
  color: #999;
}

.empty-tip i {
  font-size: 64px;
  margin-bottom: 16px;
  color: #20B486;
  opacity: 0.5;
}

.empty-tip p {
  font-size: 14px;
}

/* 公告列表标题 */
.notice-list-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin: 30px 0 20px;
}

.header-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, #ddd, transparent);
}

.header-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title i {
  color: #20B486;
  font-size: 24px;
}

/* 公告列表 */
.notice-list {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  width: 90%;
}

.notice-item {
  display: flex;
  align-items: center;
  padding: 18px 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background-color: #f8f9fa;
  transform: translateX(4px);
}

.notice-item.active {
  background: linear-gradient(90deg, #e8f5e9 0%, #f8f9fa 100%);
  border-left: 4px solid #20B486;
}

.item-badge {
  width: 36px;
  height: 36px;
  background: #f0f2f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #20B486;
  margin-right: 16px;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.notice-item.active .item-badge {
  background: #20B486;
  color: white;
}

.notice-item:hover .item-badge {
  background: #e8f5e9;
  color: #20B486;
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  display: flex;
  gap: 24px;
  font-size: 12px;
  color: #999;
}

.item-meta i {
  margin-right: 4px;
}

.item-arrow {
  font-size: 18px;
  transition: all 0.3s ease;
}

.notice-item:hover .item-arrow {
  color: #20B486;
  transform: translateX(4px);
}

/* 分页样式 */
.notice-page-fenye {
  margin-top: 30px;
  text-align: center;
}

.notice-page-fenye ::v-deep .el-pagination {
  padding: 0;
}

.notice-page-fenye ::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #20B486;
}

/* 响应式 */
@media (max-width: 768px) {
  .notice-page-content {
    width: 95%;
    padding: 20px 0;
  }

  .detail-header {
    padding: 16px 20px;
  }

  .detail-title {
    font-size: 18px;
  }

  .detail-content {
    padding: 20px;
    font-size: 14px;
  }

  .notice-item {
    padding: 12px 16px;
  }

  .item-title {
    font-size: 14px;
  }

  .item-badge {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }
}
</style>