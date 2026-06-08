<template>
  <div class="index">
    <headerPage></headerPage>
    <div class="index-search">
      <div class="index-search-content">
        <div class="search-content">
          <div class="content1">开启精彩学习之旅</div>
          <div class="content2">Start an exciting learning journey</div>
          <div class="content3">本平台提供海量<span style="color:#20B486">中小学信息技术课程和各种编程语言</span>的教学资源！不管你是想提升学校里的信息技术能力，还是探索编程的奇妙世界，这里都能满足你哟！</div>
          <div class="content5">快来一起学习吧！</div>
          <div class="content6">Come and let's study together！</div>
          <el-input class="searchKey" v-model="searchKey" suffix-icon="el-icon-search" placeholder="请输入想要搜索的课程名称并按回车键搜索" @change="searchTask()"></el-input>
        </div>
      </div>
      <div class="index-image">
        <img class="index-search-image" src="../../assets/image/index/index-image.png"></img>
      </div>
    </div>

    <!-- 课程推荐 -->
    <div class="most-course">
      <div class="most-content">
        <div class="page-title">
          <div style="letter-spacing: 2px;">热门课程推荐</div>
          <div style="margin-top:10px;color:#92969B">Hot <span style="color:#20B486"> Courses</span></div>
        </div>
        <div class="course-list">
          <div v-for="(item,index) in hotCourses" :key="index" class="course-item" @click="toCourseDetail(item.id)">
            <div class="course-image">
              <img :src="$store.state.HOST + item.image" style="width:100%;height:100%;object-fit:cover">
            </div>
            <div class="course-info">
              <div class="course-title">{{item.name}}</div>
              <div class="course-desc">{{item.task_describer}}</div>
              <div class="course-stats">
                <span class="stat-item">
                  <i class="el-icon-star-on"></i> {{item.collectCount || 0}} 收藏
                </span>
                <span class="stat-item">
                  <i class="el-icon-user"></i> {{item.num || 0}} 学生
                </span>
                <span class="stat-item">
                  <i class="el-icon-chat-dot-round"></i> {{item.commentCount || 0}} 评论
                </span>
              </div>
            </div>
          </div>
        </div>
        <div class="task-btns">
          <el-button @click="toTask" size="mini" type="success" icon="el-icon-arrow-right">更多课程</el-button>
        </div>
      </div>
    </div>

    <!-- 热门帖子推荐 -->
    <div class="most-forum">
      <div class="most-content">
        <div class="page-title">
          <div style="letter-spacing: 2px;">热门帖子推荐</div>
          <div style="margin-top:10px;color:#92969B">Hot <span style="color:#20B486"> Posts</span></div>
        </div>
        <div class="forum-list">
          <div v-for="(item,index) in hotForums" :key="index" class="forum-item" @click="toForumInfo(item.id)">
            <div class="forum-header">
              <div class="forum-user">
                <img class="forum-avatar" :src="$store.state.HOST + item.avatar" @error="handleAvatarError">
                <div class="forum-user-info">
                  <span class="forum-username">{{item.createBy || '匿名用户'}}</span>
                  <span class="forum-time">{{formatTime(item.createTime)}}</span>
                </div>
              </div>
            </div>
            <div class="forum-content">
              <div class="forum-title">{{item.name}}</div>
              <div class="forum-desc">{{item.content | truncate(80)}}</div>
            </div>
            <div class="forum-stats">
              <span class="stat-item">
                <i class="el-icon-thumb"></i> {{item.likeCount || 0}}
              </span>
              <span class="stat-item">
                <i class="el-icon-chat-dot-round"></i> {{item.commentCount || 0}}
              </span>
            </div>
          </div>
        </div>
        <div class="task-btns">
          <el-button @click="toForum" size="mini" type="success" icon="el-icon-arrow-right">更多帖子</el-button>
        </div>
      </div>
    </div>

    <div class="shuju">
      <div class="most-content">
        <div class="page-title">
          <div style="letter-spacing: 2px;">平台数据</div>
          <div style="margin-top:10px;color:#92969B">Our <span style="color:#20B486"> Achievement</span></div>
        </div>
        <div class="shuju-total">
          <div class="shuju-content">
            <div class="shuju-item">
              <img src="../../assets/image/index/Frame 67 (7).png">
              <div class="shuju-name">
                <div>{{data.count1}}+</div>
                <div>教师 Teacher</div>
              </div>
            </div>
            <div class="shuju-item">
              <img src="../../assets/image/index/Frame 67 (4).png">
              <div class="shuju-name">
                <div>{{data.count}}+</div>
                <div>教程 Video</div>
              </div>
            </div>
            <div class="shuju-item">
              <img src="../../assets/image/index/Frame 67 (6).png">
              <div class="shuju-name">
                <div>{{data.count2}}+</div>
                <div>学生 Student</div>
              </div>
            </div>
            <div class="shuju-item">
              <img src="../../assets/image/index/Frame 67 (5).png">
              <div class="shuju-name">
                <div>{{data.total}}+</div>
                <div>用户 Users</div>
              </div>
            </div>
          </div>
          <div class="shuju-image">
            <img style="width:50%;height:120%" src="../../assets/image/index/Group 10.png">
          </div>
        </div>
      </div>
    </div>

    <div class="liuyan">
      <div class="most-content">
        <div class="liuyan-font">
          <div class="liuyan-image">
            <img style="width:80%" src="../../assets/image/index/Group 12.png">
          </div>
          <div class="liuyan-content">
            <div>
              <div>你的任何疑问都可以留言咨询</div>
              <div style="margin-top:20px">You can leave a message for consultation if you have any questions</div>
              <el-button @click="toMessage" style="margin-top:20px" size="mini" icon="el-icon-question" type="success">留言咨询</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <bottomPage></bottomPage>
  </div>
</template>

<script>
import {getApeMajorList, getIndexAchievement, getHotCourses, getHotForums} from '../../api/api'
import headerPage from "../../components/header/header"
import bottomPage from "../../components/bottom/bottom"
export default {
  data() {
    return{
      searchKey: "",
      major: [],
      data: {
        count: 0,
        count1: 0,
        count2: 0,
        total: 0
      },
      hotCourses: [],  // 热门课程列表
      hotForums: []    // 热门帖子列表
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
    // 格式化时间方法
    formatTime(timeStr) {
      if (!timeStr) return ''

      // 处理 ISO 格式时间 (2026-04-27T13:04:34.000+00:00)
      let date = new Date(timeStr)

      // 如果解析失败，尝试其他格式
      if (isNaN(date.getTime())) {
        // 尝试替换 T 和去除毫秒
        const cleaned = timeStr.replace('T', ' ').replace(/\..+/, '')
        date = new Date(cleaned)
      }

      if (isNaN(date.getTime())) {
        // 如果还是解析失败，直接返回原字符串的前16个字符
        return timeStr.length > 16 ? timeStr.substring(0, 16) : timeStr
      }

      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')

      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    searchTask() {
      if (this.searchKey) {
        this.$router.push("/task?name=" + this.searchKey)
      }
    },
    majorTask(major) {
      this.$router.push("/task?major=" + major)
    },
    toTask() {
      this.$router.push("/task")
    },
    toForum() {
      this.$router.push("/forum")
    },
    toMessage() {
      this.$router.push("/message")
    },
    toCourseDetail(courseId) {
      this.$router.push("/taskInfo?id=" + courseId)
    },
    toForumInfo(forumId) {
      this.$router.push("/forumInfo?id=" + forumId)
    },
    getApeMajorList() {
      getApeMajorList().then(res => {
        if(res.code == 1000) {
          var major = res.data
          this.major = major.splice(0,16)
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    getIndexAchievement() {
      getIndexAchievement().then(res => {
        if(res.code == 1000) {
          this.data = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    // 获取热门课程推荐
    getHotCourses() {
      getHotCourses().then(res => {
        if(res.code == 1000) {
          this.hotCourses = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    // 获取热门帖子推荐
    getHotForums() {
      getHotForums().then(res => {
        if(res.code == 1000) {
          this.hotForums = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    }
  },
  created() {

  },
  mounted() {
    this.getApeMajorList()
    this.getIndexAchievement()
    this.getHotCourses()
    this.getHotForums()
  }
}
</script>

<style scoped>
@import url("../../assets/css/index/index.css");

/* 课程推荐样式 */
.most-course {
  width: 100%;
  padding: 50px 0;
  margin-left: 200px;
}

.course-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-top: 30px;
}

.course-item {
  width: 23%;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.course-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.course-image {
  width: 100%;
  height: 160px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 15px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item i {
  font-size: 12px;
}

/* 热门帖子推荐样式 */
.most-forum {
  width: 100%;
  padding: 50px 0;
  margin-left: 200px;
}

.forum-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 30px;
}

.forum-item {
  background: white;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border: 1px solid #f0f0f0;
}

.forum-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  border-color: #20B486;
}

.forum-header {
  margin-bottom: 12px;
}

.forum-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.forum-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.forum-user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.forum-username {
  font-size: 13px;
  font-weight: 600;
  color: #20B486;
}

.forum-time {
  font-size: 11px;
  color: #999;
}

.forum-content {
  margin-bottom: 12px;
}

.forum-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.forum-desc {
  font-size: 13px;
  line-height: 1.5;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.forum-stats {
  display: flex;
  gap: 20px;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
}

.task-btns {
  text-align: center;
  margin-top: 30px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .course-item {
    width: 31%;
  }
}

@media (max-width: 768px) {
  .course-item {
    width: 48%;
  }
  .forum-list {
    grid-template-columns: 1fr;
  }
}
</style>