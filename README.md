# K12信息技术编程在线学习平台

> 面向K12师生的信息技术编程在线学习平台，支持学生在线学习、教师课程管理、管理员审核等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot、Shiro、Redis、MyBatis、MySQL |
| 前端 | Vue、Ant Design、Axios |
| 部署 | Maven、Tomcat |

## 功能概览

- **学生端**：课程学习、在线刷题、笔记记录、论坛交流、课程收藏
- **教师端**：课程创建、资源上传、作业考试布置、学生管理
- **管理员端**：用户审核、课程审核、帖子留言审核、公告发布
- **推荐系统**：个性化课程推荐 + 热门帖子推荐

![功能模块图](screenshots/gnmkt.png)

![课程推荐算法流程图](screenshots/tjxt.png)

## 项目截图

### 登录注册

![用户登录页面](screenshots/yh-login.png)

![用户注册页面](screenshots/yh-zc.png)

![教师注册页面](screenshots/teacher-zc.png)

![管理员登录页面](screenshots/gly-login.png)

### 学生端首页

![首页](screenshots/0index.png)

![课程推荐](screenshots/kctj.png)

![帖子推荐](screenshots/tztj.png)

### 课程学习

![课程列表](screenshots/kclb.png)

![课程详情](screenshots/kcxq.png)

![章节作业](screenshots/exam.png)

![教师列表](screenshots/teacher-list.png)

![教师详情](screenshots/teaher.png)

### 论坛交流

![论坛列表](screenshots/forum.png)

![帖子详情](screenshots/post-detail.png)

### 个人中心

![个人中心](screenshots/1center.png)

![我的课程](screenshots/my-courses.png)

![我的笔记](screenshots/note.png)

![我的帖子](screenshots/myforum.png)

![我的考试](screenshots/test.png)


## 运行说明

### 后端启动
1. 导入MySQL数据库
2. 修改 application.yml 中的数据库配置
3. 运行 Spring Boot 主类

### 前端启动
npm run serve
