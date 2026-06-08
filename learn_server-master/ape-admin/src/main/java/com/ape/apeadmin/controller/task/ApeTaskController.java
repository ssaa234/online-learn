package com.ape.apeadmin.controller.task;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.*;
import com.ape.apesystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@ResponseBody
@RequestMapping("task")
public class ApeTaskController {

    @Autowired
    private ApeTaskService apeTaskService;
    @Autowired
    private ApeUserService apeUserService;
    @Autowired
    private ApeChapterService apeChapterService;
    @Autowired
    private ApeTaskCommentService apeTaskCommentService;
    @Autowired
    private ApeTaskStudentService apeTaskStudentService;
    @Autowired
    private ApeTestService apeTestService;
    @Autowired
    private ApeTestStudentService apeTestStudentService;
    @Autowired
    private ApeArticleService apeArticleService;
    @Autowired
    private ApeArticleFavorService apeArticleFavorService;
    @Autowired
    private ApeArticleCommentService apeArticleCommentService;
    @Autowired
    private ApeChapterVideoService apeChapterVideoService;
    @Autowired
    private ApeHomeworkService apeHomeworkService;
    @Autowired
    private ApeHomeworkStudentService apeHomeworkStudentService;
    @Autowired
    private ApeTaskFavorService apeTaskFavorService;

    /** 分页获取课程 */
    @Log(name = "分页获取课程", type = BusinessType.OTHER)
    @PostMapping("getApeTaskPage")
    public Result getApeTaskPage(@RequestBody ApeTask apeTask) {
        if (apeTask.getType() == 1) {
            apeTask.setTeacherId(ShiroUtils.getUserInfo().getId());
        }
        Page<ApeTask> page = new Page<>(apeTask.getPageNumber(), apeTask.getPageSize());
        QueryWrapper<ApeTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeTask.getName()), ApeTask::getName, apeTask.getName())
                .like(StringUtils.isNotBlank(apeTask.getTaskDescribe()), ApeTask::getTaskDescribe, apeTask.getTaskDescribe())
                .like(StringUtils.isNotBlank(apeTask.getTeacherName()), ApeTask::getTeacherName, apeTask.getTeacherName())
                .eq(StringUtils.isNotBlank(apeTask.getTeacherId()), ApeTask::getTeacherId, apeTask.getTeacherId())
                .eq(apeTask.getState() != null, ApeTask::getState, apeTask.getState())
                .like(StringUtils.isNotBlank(apeTask.getMajor()), ApeTask::getMajor, apeTask.getMajor())
                .like(StringUtils.isNotBlank(apeTask.getClassification()), ApeTask::getClassification, apeTask.getClassification())
                .eq(StringUtils.isNotBlank(apeTask.getGradeLevel()), ApeTask::getGradeLevel, apeTask.getGradeLevel())
                .like(StringUtils.isNotBlank(apeTask.getTextbook()), ApeTask::getTextbook, apeTask.getTextbook())
                .like(StringUtils.isNotBlank(apeTask.getGrade()), ApeTask::getGrade, apeTask.getGrade())
                .orderByDesc(ApeTask::getUpdateTime);
        Page<ApeTask> apeTaskPage = apeTaskService.page(page, queryWrapper);
        return Result.success(apeTaskPage);
    }

    /** 根据id获取课程 */
    @Log(name = "根据id获取课程", type = BusinessType.OTHER)
    @GetMapping("getApeTaskById")
    public Result getApeTaskById(@RequestParam("id")String id) {
        ApeTask apeTask = apeTaskService.getById(id);
        return Result.success(apeTask);
    }

    @GetMapping("getApeTaskByTeacher")
    public Result getApeTaskByTeacher(@RequestParam("id")String id) {
        QueryWrapper<ApeTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTask::getTeacherId,id);
        List<ApeTask> taskList = apeTaskService.list(queryWrapper);
        return Result.success(taskList);
    }

    @GetMapping("getApeTaskByTeacherId")
    public Result getApeTaskByTeacherId() {
        QueryWrapper<ApeTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTask::getTeacherId,ShiroUtils.getUserInfo().getId());
        List<ApeTask> taskList = apeTaskService.list(queryWrapper);
        return Result.success(taskList);
    }

    /** 获取课程列表 */
    @Log(name = "获取课程列表", type = BusinessType.OTHER)
    @GetMapping("getApeTaskList")
    public Result getApeTaskList() {
        List<ApeTask> taskList = apeTaskService.list();
        return Result.success(taskList);
    }

    /**
     * 获取个性化课程推荐
     * 有历史行为：按历史行为推荐相似课程
     * 无历史行为：按年龄推荐对应学段 + 热度排序
     */
    @Log(name = "获取个性化课程推荐", type = BusinessType.OTHER)
    @GetMapping("getHotCourses")
    public Result getHotCourses() {
        // 获取当前登录用户
        ApeUser currentUser = ShiroUtils.getUserInfo();

        // 根据用户年龄推断学段
        String userGradeLevel = getUserGradeLevelByAge(currentUser);

        // 查询所有已发布的课程
        QueryWrapper<ApeTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTask::getState, 0);
        List<ApeTask> taskList = apeTaskService.list(queryWrapper);

        // 如果没有课程，直接返回空列表
        if (taskList.isEmpty()) {
            return Result.success(taskList);
        }

        // 为每个课程统计热度数据（收藏数、评论数）
        for (ApeTask task : taskList) {
            // 统计收藏数量
            QueryWrapper<ApeTaskFavor> favorWrapper = new QueryWrapper<>();
            favorWrapper.lambda().eq(ApeTaskFavor::getTaskId, task.getId());
            int favorCount = apeTaskFavorService.count(favorWrapper);
            task.setCollectCount(favorCount);

            // 统计评论数量
            QueryWrapper<ApeTaskComment> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(ApeTaskComment::getTaskId, task.getId());
            int commentCount = apeTaskCommentService.count(commentWrapper);
            task.setCommentCount(commentCount);
        }

        // 获取用户历史行为
        List<String> userFavoriteTaskIds = new ArrayList<>();
        List<String> userEnrolledTaskIds = new ArrayList<>();

        if (currentUser != null) {
            // 获取用户收藏的课程
            QueryWrapper<ApeTaskFavor> favorWrapper = new QueryWrapper<>();
            favorWrapper.lambda().eq(ApeTaskFavor::getUserId, currentUser.getId());
            List<ApeTaskFavor> favorList = apeTaskFavorService.list(favorWrapper);
            userFavoriteTaskIds = favorList.stream()
                    .map(ApeTaskFavor::getTaskId)
                    .collect(Collectors.toList());

            // 获取用户已选课的课程
            QueryWrapper<ApeTaskStudent> studentWrapper = new QueryWrapper<>();
            studentWrapper.lambda().eq(ApeTaskStudent::getUserId, currentUser.getId());
            List<ApeTaskStudent> studentList = apeTaskStudentService.list(studentWrapper);
            userEnrolledTaskIds = studentList.stream()
                    .map(ApeTaskStudent::getTaskId)
                    .collect(Collectors.toList());
        }

        // 判断是否有历史行为
        boolean hasHistory = !userFavoriteTaskIds.isEmpty() || !userEnrolledTaskIds.isEmpty();

        List<ApeTask> result;

        if (hasHistory && currentUser != null) {
            // 有历史行为：按历史行为推荐相似课程
            result = recommendByHistory(taskList, currentUser, userGradeLevel,
                    userFavoriteTaskIds, userEnrolledTaskIds);
            System.out.println("=== 推荐策略：基于历史行为推荐 ===");
        } else {
            // 无历史行为：按年龄推荐对应学段 + 热度排序
            result = recommendByAge(taskList, userGradeLevel);
            System.out.println("=== 推荐策略：基于年龄学段推荐 ===");
        }

        // 打印日志
        printRecommendationLog(currentUser, userGradeLevel, hasHistory, result);

        return Result.success(result);
    }

    /**
     * 无历史行为推荐：根据年龄推荐对应学段 + 热度排序
     */
    private List<ApeTask> recommendByAge(List<ApeTask> taskList, String userGradeLevel) {
        List<ApeTask> filteredTasks = new ArrayList<>(taskList);

        // 如果有学段信息，优先推荐对应学段的课程
        if (StringUtils.isNotBlank(userGradeLevel)) {
            filteredTasks = taskList.stream()
                    .filter(task -> userGradeLevel.equals(task.getGradeLevel()))
                    .collect(Collectors.toList());

            // 如果对应学段没有课程，则推荐所有课程
            if (filteredTasks.isEmpty()) {
                filteredTasks = new ArrayList<>(taskList);
            }
        }

        // 计算热度分并排序
        for (ApeTask task : filteredTasks) {
            int hotScore = (task.getCollectCount() != null ? task.getCollectCount() : 0) * 3
                    + (task.getNum() != null ? task.getNum() : 0) * 2
                    + (task.getCommentCount() != null ? task.getCommentCount() : 0) * 1;
            task.setRecommendScore(hotScore);
        }

        // 按热度分排序
        filteredTasks.sort((a, b) -> {
            int scoreA = a.getRecommendScore() != null ? a.getRecommendScore() : 0;
            int scoreB = b.getRecommendScore() != null ? b.getRecommendScore() : 0;
            return Integer.compare(scoreB, scoreA);
        });

        // 取前8条
        return filteredTasks.stream().limit(8).collect(Collectors.toList());
    }

    /**
     * 有历史行为推荐：基于历史行为推荐相似课程
     */
    private List<ApeTask> recommendByHistory(List<ApeTask> taskList, ApeUser currentUser,
                                             String userGradeLevel, List<String> userFavoriteTaskIds,
                                             List<String> userEnrolledTaskIds) {
        // 获取用户历史课程（收藏+选课）
        List<String> historyTaskIds = new ArrayList<>();
        historyTaskIds.addAll(userFavoriteTaskIds);
        historyTaskIds.addAll(userEnrolledTaskIds);
        historyTaskIds = historyTaskIds.stream().distinct().collect(Collectors.toList());

        // 获取用户历史课程的详细信息
        QueryWrapper<ApeTask> historyWrapper = new QueryWrapper<>();
        historyWrapper.lambda().in(ApeTask::getId, historyTaskIds);
        List<ApeTask> historyTasks = apeTaskService.list(historyWrapper);

        // 从历史行为中提取用户的真实偏好
        // 统计学段分布
        Map<String, Integer> gradeLevelCount = new HashMap<>();
        // 统计分类分布（用于"其他"学段）
        Map<String, Integer> categoryCount = new HashMap<>();
        // 统计年级分布（用于K12）
        Map<String, Integer> gradeCount = new HashMap<>();

        for (ApeTask task : historyTasks) {
            String gradeLevel = task.getGradeLevel();
            if (StringUtils.isNotBlank(gradeLevel)) {
                gradeLevelCount.put(gradeLevel, gradeLevelCount.getOrDefault(gradeLevel, 0) + 1);
            }

            String classification = task.getClassification();
            if (StringUtils.isNotBlank(classification)) {
                categoryCount.put(classification, categoryCount.getOrDefault(classification, 0) + 1);
            }

            String grade = task.getGrade();
            if (StringUtils.isNotBlank(grade)) {
                gradeCount.put(grade, gradeCount.getOrDefault(grade, 0) + 1);
            }
        }

        // 确定用户的偏好学段（取历史中出现最多的学段）
        String preferredGradeLevel = null;
        int maxGradeLevelCount = 0;
        for (Map.Entry<String, Integer> entry : gradeLevelCount.entrySet()) {
            if (entry.getValue() > maxGradeLevelCount) {
                maxGradeLevelCount = entry.getValue();
                preferredGradeLevel = entry.getKey();
            }
        }

        // 确定用户的偏好分类（按出现次数排序）
        List<String> preferredCategories = categoryCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 确定用户的偏好年级（按出现次数排序）
        List<String> preferredGrades = gradeCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 打印用户偏好分析日志
        System.out.println("=== 用户历史行为分析 ===");
        System.out.println("历史课程数量: " + historyTaskIds.size());
        System.out.println("学段分布: " + gradeLevelCount);
        System.out.println("分类分布: " + categoryCount);
        System.out.println("年级分布: " + gradeCount);
        System.out.println("偏好学段: " + preferredGradeLevel);
        System.out.println("偏好分类: " + preferredCategories);
        System.out.println("偏好年级: " + preferredGrades);

        // 根据历史行为偏好计算推荐分数
        for (ApeTask task : taskList) {
            int score = 0;

            // 基础热度分（权重降低，优先个性化）
            int hotScore = (task.getCollectCount() != null ? task.getCollectCount() : 0) * 1
                    + (task.getNum() != null ? task.getNum() : 0) * 1;
            score += hotScore;

            // 1. 学段匹配（基于历史行为，而非年龄）
            if (StringUtils.isNotBlank(preferredGradeLevel)) {
                if (preferredGradeLevel.equals(task.getGradeLevel())) {
                    score += 80;  // 学段完全匹配历史偏好
                }
            } else if (StringUtils.isNotBlank(userGradeLevel)) {
                // 如果历史没有学段偏好，才使用年龄推断的学段
                if (userGradeLevel.equals(task.getGradeLevel())) {
                    score += 40;
                }
            }

            // 2. 分类匹配（用于"其他"学段的课程）
            if (!preferredCategories.isEmpty() && StringUtils.isNotBlank(task.getClassification())) {
                if (preferredCategories.contains(task.getClassification())) {
                    score += 60;  // 分类完全匹配历史偏好
                } else if (isRelatedCategory(task.getClassification(), preferredCategories)) {
                    score += 30;  // 相关分类
                }
            }

            // 3. 年级匹配（用于K12课程）
            if (!preferredGrades.isEmpty() && StringUtils.isNotBlank(task.getGrade())) {
                if (preferredGrades.contains(task.getGrade())) {
                    score += 40;  // 年级完全匹配
                } else if (isAdjacentGrade(preferredGrades, task.getGrade())) {
                    score += 20;  // 相邻年级
                }
            }

            // 4. 特殊处理：用户历史有"其他"学段课程，但当前课程是K12，降低权重
            if ("其他".equals(preferredGradeLevel) && !"其他".equals(task.getGradeLevel())) {
                score -= 50;
            }

            // 5. 特殊处理：用户历史有K12课程，但当前课程是"其他"学段，降低权重
            if (!"其他".equals(preferredGradeLevel) && preferredGradeLevel != null
                    && "其他".equals(task.getGradeLevel())) {
                score -= 30;
            }

            // 排除已收藏或已选的课程
            if (historyTaskIds.contains(task.getId())) {
                score -= 100;
            }

            task.setRecommendScore(Math.max(score, 0));
        }

        // 按推荐分排序
        taskList.sort((a, b) -> {
            int scoreA = a.getRecommendScore() != null ? a.getRecommendScore() : 0;
            int scoreB = b.getRecommendScore() != null ? b.getRecommendScore() : 0;
            return Integer.compare(scoreB, scoreA);
        });

        // 取前8条
        return taskList.stream().limit(8).collect(Collectors.toList());
    }

    /**
     * 根据年龄推断学段
     */
    private String getUserGradeLevelByAge(ApeUser user) {
        if (user == null || user.getAge() == null) {
            return null;
        }

        Integer age = user.getAge();

        if (age >= 6 && age <= 12) {
            return "小学";
        } else if (age >= 13 && age <= 15) {
            return "初中";
        } else if (age >= 16 && age <= 18) {
            return "高中";
        } else {
            return "其他";
        }
    }

    /**
     * 判断是否为相关分类（如 Java 和 Spring Boot 相关）
     */
    private boolean isRelatedCategory(String category, List<String> preferredCategories) {
        if (category == null || preferredCategories.isEmpty()) {
            return false;
        }
        // 定义分类关联关系
        Map<String, List<String>> relatedMap = new HashMap<>();
        relatedMap.put("Java", Arrays.asList("Spring Boot", "Java Web", "SSM", "Spring Cloud"));
        relatedMap.put("Python", Arrays.asList("Django", "Flask", "数据分析", "人工智能"));
        relatedMap.put("前端", Arrays.asList("Vue", "React", "JavaScript", "HTML", "CSS"));
        relatedMap.put("数据库", Arrays.asList("MySQL", "Oracle", "SQL Server", "Redis"));
        relatedMap.put("运维", Arrays.asList("Linux", "Docker", "K8s", "Jenkins"));

        for (String pref : preferredCategories) {
            if (relatedMap.containsKey(pref) && relatedMap.get(pref).contains(category)) {
                return true;
            }
            if (relatedMap.containsKey(category) && relatedMap.get(category).contains(pref)) {
                return true;
            }
            // 直接包含关系
            if (StringUtils.isNotBlank(pref) && category.contains(pref)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为相邻年级
     */
    private boolean isAdjacentGrade(List<String> preferredGrades, String taskGrade) {
        if (preferredGrades.isEmpty() || taskGrade == null) return false;

        Map<String, Integer> gradeMap = new HashMap<>();
        // 小学
        gradeMap.put("一年级", 1); gradeMap.put("二年级", 2); gradeMap.put("三年级", 3);
        gradeMap.put("四年级", 4); gradeMap.put("五年级", 5); gradeMap.put("六年级", 6);
        // 初中
        gradeMap.put("初一", 7); gradeMap.put("初二", 8); gradeMap.put("初三", 9);
        // 高中
        gradeMap.put("高一", 10); gradeMap.put("高二", 11); gradeMap.put("高三", 12);

        Integer taskLevel = gradeMap.get(taskGrade);
        if (taskLevel == null) return false;

        for (String prefGrade : preferredGrades) {
            Integer prefLevel = gradeMap.get(prefGrade);
            if (prefLevel != null && Math.abs(prefLevel - taskLevel) <= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 打印推荐日志
     */
    private void printRecommendationLog(ApeUser currentUser, String userGradeLevel,
                                        boolean hasHistory, List<ApeTask> result) {
        System.out.println("=== 个性化课程推荐结果 ===");
        if (currentUser != null) {
            System.out.println("当前用户: " + currentUser.getUserName()
                    + " | 年龄: " + currentUser.getAge()
                    + " | 推断学段: " + userGradeLevel
                    + " | 是否有历史行为: " + hasHistory);
        } else {
            System.out.println("用户未登录，返回热门课程");
        }
        System.out.println("推荐课程列表：");
        for (ApeTask task : result) {
            System.out.println(task.getName()
                    + " | 学段:" + task.getGradeLevel()
                    + " | 分类:" + task.getClassification()
                    + " | 年级:" + task.getGrade()
                    + " | 推荐分:" + task.getRecommendScore()
                    + " | 收藏:" + task.getCollectCount()
                    + " | 学生:" + task.getNum());
        }
    }

    /** 保存课程 */
    @Log(name = "保存课程", type = BusinessType.INSERT)
    @PostMapping("saveApeTask")
    public Result saveApeTask(@RequestBody ApeTask apeTask) {
        if (apeTask.getType() == 1) {
            apeTask.setTeacherId(ShiroUtils.getUserInfo().getId());
        }
        ApeUser apeUser = apeUserService.getById(apeTask.getTeacherId());
        apeTask.setTeacherName(apeUser.getUserName());
        boolean save = apeTaskService.save(apeTask);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑课程 */
    @Log(name = "编辑课程", type = BusinessType.UPDATE)
    @PostMapping("editApeTask")
    public Result editApeTask(@RequestBody ApeTask apeTask) {
        if (StringUtils.isNotBlank(apeTask.getTeacherId())) {
            ApeUser apeUser = apeUserService.getById(apeTask.getTeacherId());
            apeTask.setTeacherName(apeUser.getUserName());
        }
        boolean save = apeTaskService.updateById(apeTask);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除课程 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeApeTask")
    @Log(name = "删除课程", type = BusinessType.DELETE)
    public Result removeApeTask(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeTaskService.removeById(id);
                QueryWrapper<ApeTaskComment> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeTaskComment::getTaskId,id);
                apeTaskCommentService.remove(queryWrapper);
                QueryWrapper<ApeTaskStudent> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(ApeTaskStudent::getTaskId,id);
                apeTaskStudentService.remove(queryWrapper1);
                QueryWrapper<ApeChapter> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(ApeChapter::getTaskId,id);
                List<ApeChapter> chapterList = apeChapterService.list(queryWrapper2);
                for (ApeChapter chapter : chapterList) {
                    apeChapterService.removeById(chapter.getId());
                    QueryWrapper<ApeChapterVideo> queryWrapper3 = new QueryWrapper<>();
                    queryWrapper3.lambda().eq(ApeChapterVideo::getChapterId,chapter.getId());
                    apeChapterVideoService.remove(queryWrapper3);
                    QueryWrapper<ApeHomework> queryWrapper4 = new QueryWrapper<>();
                    queryWrapper4.lambda().eq(ApeHomework::getChapterId,chapter.getId());
                    apeHomeworkService.remove(queryWrapper4);
                    QueryWrapper<ApeHomeworkStudent> queryWrapper5 = new QueryWrapper<>();
                    queryWrapper5.lambda().eq(ApeHomeworkStudent::getChapterId,chapter.getId());
                    apeHomeworkStudentService.remove(queryWrapper5);
                }
                QueryWrapper<ApeArticle> queryWrapper3 = new QueryWrapper<>();
                queryWrapper3.lambda().eq(ApeArticle::getTaskId,id);
                List<ApeArticle> articleList = apeArticleService.list(queryWrapper3);
                for (ApeArticle apeArticle : articleList) {
                    apeArticleService.removeById(apeArticle.getId());
                    QueryWrapper<ApeArticleFavor> queryWrapper4 = new QueryWrapper<>();
                    queryWrapper4.lambda().eq(ApeArticleFavor::getArticleId,apeArticle.getId());
                    apeArticleFavorService.remove(queryWrapper4);
                    QueryWrapper<ApeArticleComment> queryWrapper5 = new QueryWrapper<>();
                    queryWrapper5.lambda().eq(ApeArticleComment::getTaskId,apeArticle.getId());
                    apeArticleCommentService.remove(queryWrapper5);
                }
                QueryWrapper<ApeTest> queryWrapper5 = new QueryWrapper<>();
                queryWrapper5.lambda().eq(ApeTest::getTaskId,id);
                List<ApeTest> testList = apeTestService.list(queryWrapper5);
                for (ApeTest apeTest : testList) {
                    apeTestService.removeById(apeTest.getId());
                    QueryWrapper<ApeTestStudent> queryWrapper6 = new QueryWrapper<>();
                    queryWrapper6.lambda().eq(ApeTestStudent::getTestId,apeTest.getId());
                    apeTestStudentService.remove(queryWrapper6);
                }
                // 删除课程收藏记录
                QueryWrapper<ApeTaskFavor> favorWrapper = new QueryWrapper<>();
                favorWrapper.lambda().eq(ApeTaskFavor::getTaskId, id);
                apeTaskFavorService.remove(favorWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("课程id不能为空！");
        }
    }
}