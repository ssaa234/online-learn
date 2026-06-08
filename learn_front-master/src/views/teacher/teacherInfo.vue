<template>
  <div class="teacherInfo">
    <headerPage></headerPage>
    <div class="teacherInfo-content">
      <!-- 教师头部信息 -->
      <div class="teacher-header">
        <div class="teacher-avatar">
          <img :src="getAvatarUrl(teacher.avatar)">
          <div class="teacher-badge" v-if="teacher.agree">
            {{ teacher.agree }}
          </div>
        </div>
        <div class="teacher-info">
          <div class="teacher-name">
            {{ teacher.userName }}
            <span class="teacher-sex" v-if="teacher.sex == 0">👨 男老师</span>
            <span class="teacher-sex" v-else-if="teacher.sex == 1">👩 女老师</span>
          </div>
          <div class="teacher-stats">
            <div class="stat-item">
              <i class="el-icon-collection"></i>
              <span>{{ teacher.courseCount || 0 }}</span>
              <label>门课程</label>
            </div>
            <div class="stat-item">
              <i class="el-icon-user"></i>
              <span>{{ teacher.studentCount || 0 }}</span>
              <label>名学生</label>
            </div>
            <div class="stat-item" v-if="teacher.school">
              <i class="el-icon-school"></i>
              <span>{{ teacher.school }}</span>
              <label>单位</label>
            </div>
          </div>
          <div class="teacher-remark-box">
            <div class="remark-title">
              <i class="el-icon-chat-line-square"></i> 老师简介
            </div>
            <div class="remark-content">
              {{ teacher.remark || '这位老师很认真，还没有留下介绍~' }}
            </div>
          </div>
        </div>
      </div>

      <!-- 课程列表区域 -->
      <div class="course-section">
        <div class="section-title">
          <i class="el-icon-notebook-2"></i>
          老师的课程（{{ task.length }}门）
          <span class="title-line"></span>
        </div>

        <div class="course-list">
          <div
              @click="toTask(item.id)"
              v-for="(item,index) in task"
              :key="index"
              class="course-card">
            <div class="course-icon">
              <i class="el-icon-video-camera"></i>
            </div>
            <div class="course-info">
              <div class="course-name">{{ item.name }}</div>
              <div class="course-desc">{{ item.taskDescribe || '点击查看课程详情' }}</div>
            </div>
            <div class="course-arrow">
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="task.length === 0" class="empty-courses">
            <i class="el-icon-folder-opened"></i>
            <p>暂无课程，敬请期待~</p>
          </div>
        </div>
      </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {getUserById, getApeTaskByTeacher} from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"

export default {
  data() {
    return{
      teacherId: "",
      teacher: {},
      task: []
    }
  },
  components: {
    headerPage,
    bottomPage
  },
  methods: {
    toTask(id) {
      this.$router.push("/taskInfo?id=" + id)
    },
    getAvatarUrl(avatar) {
      if (!avatar) {
        return ''
      }
      if (avatar.startsWith('http')) {
        return avatar
      }
      return this.$store.state.HOST + avatar
    }
  },
  mounted() {
    var teacherId = this.$route.query.id
    if (teacherId) {
      this.teacherId = teacherId
    }

    // 获取教师信息
    getUserById({id: this.teacherId}).then(res => {
      if (res.code == 1000) {
        this.teacher = res.data
      }
    })

    // 获取教师的课程列表
    getApeTaskByTeacher({id: this.teacherId}).then(res => {
      if (res.code == 1000) {
        this.task = res.data
      }
    })
  }
}
</script>

<style scoped>
.teacherInfo {
  width: 100%;
  height: 100%;
}

.teacherInfo-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 24px 60px;
}

/* 教师头部卡片 */
.teacher-header {
  background: white;
  border-radius: 28px;
  padding: 32px;
  margin-bottom: 32px;
  display: flex;
  gap: 32px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  transition: transform 0.3s ease;
}

/* 头像区域 */
.teacher-avatar {
  flex-shrink: 0;
  position: relative;
}

.teacher-avatar img {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  object-fit: cover;
  border: 5px solid #667eea;
  box-shadow: 0 8px 20px rgba(102,126,234,0.3);
  background: #f0f0f0;
}

.teacher-badge {
  position: absolute;
  bottom: 90px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #f5c542 0%, #e6b800 100%);
  color: #fff;
  font-size: 15px;
  padding: 4px 12px;
  border-radius: 20px;
  white-space: nowrap;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 教师信息区域 */
.teacher-info {
  flex: 1;
}

.teacher-name {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.teacher-sex {
  font-size: 14px;
  font-weight: normal;
  background: #f0f0f0;
  padding: 4px 12px;
  border-radius: 20px;
  color: #666;
}

/* 统计数据 */
.teacher-stats {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
  background: #f8f9fc;
  padding: 12px 20px;
  border-radius: 20px;
  min-width: 100px;
}

.stat-item i {
  font-size: 24px;
  color: #667eea;
  display: block;
  margin-bottom: 6px;
}

.stat-item span {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  display: block;
  line-height: 1.2;
}

.stat-item label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  display: block;
}

/* 老师简介 */
.teacher-remark-box {
  background: #f8f9fc;
  border-radius: 20px;
  padding: 16px 20px;
}

.remark-title {
  font-size: 14px;
  font-weight: 500;
  color: #667eea;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.remark-title i {
  font-size: 16px;
}

.remark-content {
  font-size: 14px;
  color: #555;
  line-height: 1.7;
  word-break: break-word;
}

/* 课程区域 */
.course-section {
  background: white;
  border-radius: 28px;
  padding: 24px 28px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.05);
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  font-size: 24px;
  color: #667eea;
}

.title-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, #667eea 0%, transparent 100%);
  margin-left: 10px;
}

/* 课程卡片列表 */
.course-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-card {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fc;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  gap: 16px;
}

.course-card:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: translateX(6px);
  box-shadow: 0 6px 16px rgba(102,126,234,0.3);
}

.course-card:hover .course-icon i,
.course-card:hover .course-name,
.course-card:hover .course-desc,
.course-card:hover .course-arrow i {
  color: white;
}

.course-icon {
  flex-shrink: 0;
}

.course-icon i {
  font-size: 32px;
  color: #667eea;
  transition: color 0.3s ease;
}

.course-info {
  flex: 1;
}

.course-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  transition: color 0.3s ease;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-desc {
  font-size: 13px;
  color: #999;
  transition: color 0.3s ease;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-arrow i {
  font-size: 20px;
  color: #ccc;
  transition: all 0.3s ease;
}

/* 空状态 */
.empty-courses {
  text-align: center;
  padding: 50px 20px;
}

.empty-courses i {
  font-size: 64px;
  color: #ddd;
}

.empty-courses p {
  margin-top: 16px;
  color: #999;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .teacherInfo-content {
    padding: 16px;
  }

  .teacher-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 24px;
  }

  .teacher-name {
    justify-content: center;
  }

  .teacher-stats {
    justify-content: center;
  }

  .section-title {
    font-size: 18px;
  }

  .course-card {
    padding: 12px 16px;
  }

  .course-name {
    font-size: 14px;
  }

  .teacher-avatar img {
    width: 120px;
    height: 120px;
  }
}
</style>