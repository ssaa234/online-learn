package com.ape.apeadmin.controller.user;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apecommon.utils.PasswordUtils;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.*;
import com.ape.apesystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@ResponseBody
@RequestMapping("/user")
public class ApeUserController {

    @Autowired
    private ApeUserService apeUserService;
    @Autowired
    private ApeRoleService apeRoleService;
    @Autowired
    private ApeUserRoleService apeUserRoleService;
    @Autowired
    private ApeTaskService apeTaskService;
    @Autowired
    private ApeTaskStudentService apeTaskStudentService;

    /** 更新用户状态（启用/禁用） */
    @Log(name = "更新用户状态", type = BusinessType.UPDATE)
    @PostMapping("updateUserStatus")
    public Result updateUserStatus(@RequestBody JSONObject json) {
        String id = json.getString("id");
        Integer status = json.getInteger("status");
        ApeUser user = new ApeUser();
        user.setId(id);
        user.setStatus(status);
        boolean update = apeUserService.updateById(user);
        if (update) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 分页查询用户（带教师统计和综合排序） */
    @Log(name = "分页查询用户", type = BusinessType.OTHER)
    @PostMapping("getUserPage")
    public Result getUserPage(@RequestBody ApeUser apeUser) {
        Page<ApeUser> page = new Page<>(apeUser.getPageNumber(), apeUser.getPageSize());
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeUser.getUserName()), ApeUser::getUserName, apeUser.getUserName())
                .like(StringUtils.isNotBlank(apeUser.getSchool()), ApeUser::getSchool, apeUser.getSchool())
                .eq(apeUser.getUserType() != null, ApeUser::getUserType, apeUser.getUserType())
                .eq(apeUser.getStatus() != null, ApeUser::getStatus, apeUser.getStatus());

        Page<ApeUser> userPage = apeUserService.page(page, queryWrapper);

        // 如果是教师类型，统计课程数和学生数，并进行综合排序
        if (apeUser.getUserType() == null || apeUser.getUserType() == 1) {
            addTeacherStatsAndSort(userPage);
        }

        return Result.success(userPage);
    }

    /**
     * 为教师用户添加课程数和学生数统计，并进行综合排序
     * 排序规则：学生数 > 课程数 > 创建时间
     */
    private void addTeacherStatsAndSort(Page<ApeUser> userPage) {
        if (userPage.getRecords().isEmpty()) {
            return;
        }

        // 获取所有教师ID
        List<String> teacherIds = userPage.getRecords().stream()
                .filter(u -> u.getUserType() == 1)
                .map(ApeUser::getId)
                .collect(Collectors.toList());

        if (teacherIds.isEmpty()) {
            return;
        }

        // 1. 统计每个教师的课程数量
        QueryWrapper<ApeTask> taskWrapper = new QueryWrapper<>();
        taskWrapper.lambda().in(ApeTask::getTeacherId, teacherIds);
        List<ApeTask> taskList = apeTaskService.list(taskWrapper);

        Map<String, Integer> courseCountMap = new HashMap<>();
        for (ApeTask task : taskList) {
            String teacherId = task.getTeacherId();
            courseCountMap.put(teacherId, courseCountMap.getOrDefault(teacherId, 0) + 1);
        }

        // 2. 建立教师ID -> 课程ID列表的映射
        Map<String, List<String>> teacherCourseMap = new HashMap<>();
        for (ApeTask task : taskList) {
            String teacherId = task.getTeacherId();
            teacherCourseMap.computeIfAbsent(teacherId, k -> new ArrayList<>()).add(task.getId());
        }

        // 3. 统计每个教师的学生数量（去重）
        Map<String, Set<String>> teacherStudentSet = new HashMap<>();
        if (!taskList.isEmpty()) {
            // 收集所有课程ID
            List<String> allCourseIds = taskList.stream()
                    .map(ApeTask::getId)
                    .collect(Collectors.toList());

            // 查询选课记录
            QueryWrapper<ApeTaskStudent> studentWrapper = new QueryWrapper<>();
            studentWrapper.lambda()
                    .in(ApeTaskStudent::getTaskId, allCourseIds)
                    .eq(ApeTaskStudent::getState, 0);  // 只统计已选课的学生
            List<ApeTaskStudent> studentList = apeTaskStudentService.list(studentWrapper);

            // 按教师统计学生（去重）
            for (ApeTaskStudent student : studentList) {
                String taskId = student.getTaskId();
                String userId = student.getUserId();
                // 找到这个课程对应的教师ID
                for (Map.Entry<String, List<String>> entry : teacherCourseMap.entrySet()) {
                    if (entry.getValue().contains(taskId)) {
                        teacherStudentSet.computeIfAbsent(entry.getKey(), k -> new HashSet<>()).add(userId);
                        break;
                    }
                }
            }
        }

        // 4. 填充统计数据到用户对象
        for (ApeUser user : userPage.getRecords()) {
            if (user.getUserType() == 1) {
                String teacherId = user.getId();
                user.setCourseCount(courseCountMap.getOrDefault(teacherId, 0));
                user.setStudentCount(teacherStudentSet.getOrDefault(teacherId, new HashSet<>()).size());
            } else {
                user.setCourseCount(0);
                user.setStudentCount(0);
            }
        }

        // 5. 综合排序：学生数 > 课程数 > 创建时间
        List<ApeUser> records = userPage.getRecords();
        records.sort((a, b) -> {
            // 第一优先级：学生数多的排前面（降序）
            int studentCompare = Integer.compare(b.getStudentCount(), a.getStudentCount());
            if (studentCompare != 0) return studentCompare;

            // 第二优先级：学生数相同时，课程数多的排前面（降序）
            int courseCompare = Integer.compare(b.getCourseCount(), a.getCourseCount());
            if (courseCompare != 0) return courseCompare;

            // 第三优先级：都相同时，创建时间新的排前面（降序）
            if (a.getCreateTime() != null && b.getCreateTime() != null) {
                return b.getCreateTime().compareTo(a.getCreateTime());
            }
            if (a.getCreateTime() != null) return -1;
            if (b.getCreateTime() != null) return 1;

            return 0;
        });

        // 重新设置排序后的记录
        userPage.setRecords(records);
    }

    /** 根据类型查询用户 */
    @Log(name = "根据类型查询用户", type = BusinessType.OTHER)
    @GetMapping("getUserListByType")
    public Result getUserListByType(@RequestParam("type")Integer type) {
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getUserType,type);
        List<ApeUser> userList = apeUserService.list(queryWrapper);
        return Result.success(userList);
    }

    /** 根据id查询用户（带教师统计） */
    @Log(name = "根据id查询用户", type = BusinessType.OTHER)
    @GetMapping("getUserById")
    public Result getUserById(@RequestParam("id") String id) {
        ApeUser apeUser = apeUserService.getById(id);

        // 如果是教师类型，统计课程数和学生数
        if (apeUser.getUserType() == 1) {
            // 统计课程数
            QueryWrapper<ApeTask> taskWrapper = new QueryWrapper<>();
            taskWrapper.lambda().eq(ApeTask::getTeacherId, apeUser.getId());
            int courseCount = apeTaskService.count(taskWrapper);
            apeUser.setCourseCount(courseCount);

            // 统计学生数（通过课程关联选课表，去重）
            List<ApeTask> taskList = apeTaskService.list(taskWrapper);
            Set<String> studentIds = new HashSet<>();
            for (ApeTask task : taskList) {
                QueryWrapper<ApeTaskStudent> studentWrapper = new QueryWrapper<>();
                studentWrapper.lambda()
                        .eq(ApeTaskStudent::getTaskId, task.getId())
                        .eq(ApeTaskStudent::getState, 0);
                List<ApeTaskStudent> studentList = apeTaskStudentService.list(studentWrapper);
                for (ApeTaskStudent student : studentList) {
                    studentIds.add(student.getUserId());
                }
            }
            apeUser.setStudentCount(studentIds.size());
        }

        QueryWrapper<ApeUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUserRole::getUserId, apeUser.getId());
        List<ApeUserRole> list = apeUserRoleService.list(queryWrapper);
        List<String> roles = new ArrayList<>();
        for (ApeUserRole apeUserRole : list) {
            roles.add(apeUserRole.getRoleId());
        }
        apeUser.setRoleIds(roles);
        return Result.success(apeUser);
    }

    /** 新增用户 */
    @Log(name = "新增用户", type = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveUser")
    public Result saveUser(@RequestBody ApeUser apeUser) {
        System.out.println("接收到的用户数据: " + apeUser);
        System.out.println("手机号: " + apeUser.getTel());
        //先校验登陆账号是否重复
        boolean account = checkAccount(apeUser);
        if (!account) {
            return Result.fail("登陆账号已存在不可重复！");
        }
        String uuid = IdWorker.get32UUID();
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(apeUser.getPassword());
        String[] split = encrypt.split("\\$");
        apeUser.setId(uuid);
        apeUser.setPassword(split[0]);
        apeUser.setSalt(split[1]);
        apeUser.setAvatar("/img/avatar.jpg");
        apeUser.setPwdUpdateDate(new Date());
        apeUser.setTel(apeUser.getTel());
        //保存用户
        boolean save = apeUserService.save(apeUser);
        //再保存用户角色关系
        List<String> roleIds = apeUser.getRoleIds();
        List<ApeUserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                ApeUserRole apeUserRole = new ApeUserRole();
                apeUserRole.setUserId(uuid);
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        if (apeUser.getUserType() == 1) {
            ApeUserRole apeUserRole = new ApeUserRole();
            apeUserRole.setUserId(uuid);
            QueryWrapper<ApeRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ApeRole::getRoleKey,"teacher");
            ApeRole role = apeRoleService.getOne(queryWrapper);
            apeUserRole.setRoleId(role.getId());
            apeUserRoles.add(apeUserRole);
        }
        apeUserRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 编辑用户 */
    @Log(name = "编辑用户", type = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("editUser")
    public Result editUser(@RequestBody ApeUser apeUser) {
        ApeUser user = apeUserService.getById(apeUser.getId());
        if (!user.getLoginAccount().equals(apeUser.getLoginAccount())) {
            //先校验登陆账号是否重复
            boolean account = checkAccount(apeUser);
            if (!account) {
                return Result.fail("登陆账号已存在不可重复！");
            }
        }
        //更新用户
        boolean edit = apeUserService.updateById(apeUser);
        //先删除用户角色关系
        QueryWrapper<ApeUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUserRole::getUserId,apeUser.getId());
        apeUserRoleService.remove(queryWrapper);
        //再次保存最新的关系
        List<String> roleIds = apeUser.getRoleIds();
        List<ApeUserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                ApeUserRole apeUserRole = new ApeUserRole();
                apeUserRole.setUserId(apeUser.getId());
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        if (apeUser.getUserType() == 1) {
            ApeUserRole apeUserRole = new ApeUserRole();
            apeUserRole.setUserId(apeUser.getId());
            QueryWrapper<ApeRole> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(ApeRole::getRoleKey,"teacher");
            ApeRole role = apeRoleService.getOne(queryWrapper1);
            apeUserRole.setRoleId(role.getId());
            apeUserRoles.add(apeUserRole);
        }
        apeUserRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 校验用户 */
    public boolean checkAccount(ApeUser apeUser) {
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getLoginAccount,apeUser.getLoginAccount());
        int count = apeUserService.count(queryWrapper);
        return count <= 0;
    }

    /** 删除用户 */
    @Log(name = "删除用户", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeUser")
    public Result removeUser(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                boolean remove = apeUserService.removeById(id);
                QueryWrapper<ApeUserRole> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeUserRole::getUserId,id);
                apeUserRoleService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("角色id不能为空！");
        }
    }

    @Log(name = "修改密码", type = BusinessType.UPDATE)
    @PostMapping("changePassword")
    public Result changePassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String password = json.getString("password");
        ApeUser apeUser = apeUserService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(password, apeUser.getPassword() + "$" + apeUser.getSalt());
        if (decrypt) {
            String newPassword = json.getString("newPassword");
            String encrypt = PasswordUtils.encrypt(newPassword);
            String[] split = encrypt.split("\\$");
            apeUser.setSalt(split[1]);
            apeUser.setPassword(split[0]);
            apeUser.setPwdUpdateDate(new Date());
            boolean update = apeUserService.updateById(apeUser);
            if (update) {
                return Result.success();
            } else {
                return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        } else {
            return Result.fail("旧密码不正确");
        }
    }

    /** 重置密码 */
    @Log(name = "重置密码", type = BusinessType.UPDATE)
    @PostMapping("resetPassword")
    public Result resetPassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String newPassword = json.getString("newPassword");
        String encrypt = PasswordUtils.encrypt(newPassword);
        String[] split = encrypt.split("\\$");
        ApeUser apeUser = apeUserService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(newPassword, apeUser.getPassword() + "$" + apeUser.getSalt());
        if (decrypt) {
            return Result.fail("新密码不可和旧密码相同！");
        }
        apeUser.setPassword(split[0]);
        apeUser.setSalt(split[1]);
        apeUser.setPwdUpdateDate(new Date());
        boolean update = apeUserService.updateById(apeUser);
        if (update) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 获取登陆用户信息 */
    @Log(name = "获取登陆用户信息", type = BusinessType.OTHER)
    @GetMapping("getUserInfo")
    public Result getUserInfo() {
        ApeUser user = ShiroUtils.getUserInfo();
        ApeUser apeUser = apeUserService.getById(user.getId());
        return Result.success(apeUser);
    }

    /** 修改个人信息 */
    @Log(name = "修改个人信息", type = BusinessType.UPDATE)
    @PostMapping("setUserInfo")
    public Result setUserInfo(@RequestBody ApeUser apeUser) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeUser.setId(userInfo.getId());
        apeUserService.updateById(apeUser);
        return Result.success();
    }

    /** 修改个人头像 */
    @Log(name = "修改个人头像", type = BusinessType.UPDATE)
    @PostMapping("setUserAvatar/{id}")
    public Result setUserAvatar(@PathVariable("id") String id,@RequestParam("file") MultipartFile avatar) {
        if(StringUtils.isBlank(id)){
            return Result.fail("用户id为空!");
        }
        ApeUser apeUser = apeUserService.getById(id);
        if(avatar.isEmpty()){
            return Result.fail("上传的头像不能为空!");
        }
        String coverType = avatar.getOriginalFilename().substring(avatar.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //文件名=当前时间到毫秒+原来的文件名
            String fileName = id + "."+ coverType;
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //现在的文件地址
            if (StringUtils.isNotBlank(apeUser.getAvatar())) {
                String s = apeUser.getAvatar().split("/")[2];
                File now = new File(filePath + System.getProperty("file.separator") + s);
                boolean delete = now.delete();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                avatar.transferTo(dest);
                //更新头像
                apeUser.setAvatar(storeImgPath);
                apeUserService.updateById(apeUser);
                return Result.success(storeImgPath);
            } catch (IOException e) {
                return Result.fail("上传失败");
            }
        } else {
            return Result.fail("请选择正确的图片格式");
        }
    }
}