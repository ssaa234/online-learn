package com.ape.apeadmin.controller.forum;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeForum;
import com.ape.apesystem.domain.ApeForumItem;
import com.ape.apesystem.domain.ApeForumLike;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeForumItemService;
import com.ape.apesystem.service.ApeForumLikeService;
import com.ape.apesystem.service.ApeForumService;
import com.ape.apesystem.service.ApeUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

@Controller
@ResponseBody
@RequestMapping("forum")
public class ApeForumController {

    @Autowired
    private ApeForumService apeForumService;

    @Autowired
    private ApeForumItemService apeForumItemService;

    @Autowired
    private ApeForumLikeService apeForumLikeService;

    @Autowired
    private ApeUserService apeUserService;

    /** 获取我的帖子（当前用户发布的） */
    @PostMapping("getMyForumPage")
    public Result getMyForumPage(@RequestBody ApeForum apeForum) {
        String userId = ShiroUtils.getUserInfo().getId();
        Page<ApeForum> page = new Page<>(apeForum.getPageNumber(), apeForum.getPageSize());
        QueryWrapper<ApeForum> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeForum::getUserId, userId)
                .orderByDesc(ApeForum::getCreateTime);
        Page<ApeForum> forumPage = apeForumService.page(page, queryWrapper);

        // 补充点赞数和评论数
        for (ApeForum forum : forumPage.getRecords()) {
            // 统计点赞数
            QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.lambda().eq(ApeForumLike::getTargetId, forum.getId()).eq(ApeForumLike::getType, 1);
            forum.setLikeCount(apeForumLikeService.count(likeWrapper));
            // 统计评论数
            QueryWrapper<ApeForumItem> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(ApeForumItem::getForumId, forum.getId());
            forum.setCommentCount(apeForumItemService.count(commentWrapper));
        }

        return Result.success(forumPage);
    }

    /** 删除我的帖子（只能删除自己发布的） */
    @GetMapping("removeMyForum")
    @Log(name = "删除我的帖子", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeMyForum(@RequestParam("id") String id) {
        String userId = ShiroUtils.getUserInfo().getId();

        // 查询帖子是否存在
        ApeForum forum = apeForumService.getById(id);
        if (forum == null) {
            return Result.fail("帖子不存在");
        }

        // 验证是否是自己的帖子
        if (!forum.getUserId().equals(userId)) {
            return Result.fail("只能删除自己发布的帖子");
        }

        // 删除帖子
        boolean remove = apeForumService.removeById(id);
        if (remove) {
            // 删除相关的评论
            QueryWrapper<ApeForumItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.lambda().eq(ApeForumItem::getForumId, id);
            apeForumItemService.remove(itemWrapper);
            // 删除相关的点赞
            QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.lambda().eq(ApeForumLike::getTargetId, id);
            apeForumLikeService.remove(likeWrapper);
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 获取热门帖子推荐 - 按热度（点赞量 + 评论量）排序 */
    @GetMapping("getHotForums")
    public Result getHotForums() {
        // 查询所有帖子（不限数量，先全部查出来计算热度）
        QueryWrapper<ApeForum> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(ApeForum::getCreateTime);
        List<ApeForum> forumList = apeForumService.list(queryWrapper);

        // 用于存储带热度的帖子
        List<HotForumVO> hotForumList = new ArrayList<>();

        // 为每个帖子统计点赞数和评论数，并计算热度
        for (ApeForum forum : forumList) {
            HotForumVO hotForum = new HotForumVO();

            // 复制基本信息
            hotForum.setId(forum.getId());
            hotForum.setName(forum.getName());
            hotForum.setContent(forum.getContent());
            hotForum.setCreateBy(forum.getCreateBy());
            hotForum.setCreateTime(forum.getCreateTime());
            hotForum.setUserId(forum.getUserId());

            // 获取用户头像
            ApeUser user = apeUserService.getById(forum.getUserId());
            if (user != null) {
                hotForum.setAvatar(user.getAvatar());
                hotForum.setUserName(user.getUserName());
            }

            // 统计点赞数（type=1 表示帖子点赞）
            QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.lambda().eq(ApeForumLike::getTargetId, forum.getId())
                    .eq(ApeForumLike::getType, 1);
            int likeCount = apeForumLikeService.count(likeWrapper);
            hotForum.setLikeCount(likeCount);

            // 统计评论数
            QueryWrapper<ApeForumItem> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(ApeForumItem::getForumId, forum.getId());
            int commentCount = apeForumItemService.count(commentWrapper);
            hotForum.setCommentCount(commentCount);

            // 计算热度分 = 点赞数 × 60 + 评论数 × 40
            int hotScore = likeCount * 6 + commentCount * 4;
            hotForum.setHotScore(hotScore);

            hotForumList.add(hotForum);
        }

        // 按热度分降序排序
        hotForumList.sort((a, b) -> Integer.compare(b.getHotScore(), a.getHotScore()));

        // 取前6条
        List<HotForumVO> result = hotForumList.stream().limit(6).collect(Collectors.toList());

        return Result.success(result);
    }

    /** 分页获取论坛 */
    @PostMapping("getApeForumPage")
    public Result getApeForumPage(@RequestBody ApeForum apeForum) {
        Page<ApeForum> page = new Page<>(apeForum.getPageNumber(), apeForum.getPageSize());
        QueryWrapper<ApeForum> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeForum.getName()), ApeForum::getName, apeForum.getName())
                .like(StringUtils.isNotBlank(apeForum.getContent()), ApeForum::getContent, apeForum.getContent())
                .eq(StringUtils.isNotBlank(apeForum.getUserId()), ApeForum::getUserId, apeForum.getUserId())
                .orderByDesc(ApeForum::getCreateTime);
        Page<ApeForum> apeForumPage = apeForumService.page(page, queryWrapper);

        // 补充用户头像、用户名、点赞数、评论数
        for (ApeForum forum : apeForumPage.getRecords()) {
            // 获取用户信息
            if (StringUtils.isNotBlank(forum.getUserId())) {
                ApeUser user = apeUserService.getById(forum.getUserId());
                if (user != null) {
                    forum.setAvatar(user.getAvatar());
                    forum.setUserName(user.getUserName());
                }
            }

            // 统计点赞数（type=1 表示帖子点赞）
            QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.lambda().eq(ApeForumLike::getTargetId, forum.getId())
                    .eq(ApeForumLike::getType, 1);
            int likeCount = apeForumLikeService.count(likeWrapper);
            forum.setLikeCount(likeCount);

            // 统计评论数
            QueryWrapper<ApeForumItem> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(ApeForumItem::getForumId, forum.getId());
            int commentCount = apeForumItemService.count(commentWrapper);
            forum.setCommentCount(commentCount);
        }

        return Result.success(apeForumPage);
    }

    /** 根据id获取论坛 */
    @GetMapping("getApeForumById")
    public Result getApeForumById(@RequestParam("id")String id) {
        ApeForum apeForum = apeForumService.getById(id);
        if (apeForum != null) {
            // 通过 user_id 获取用户信息
            ApeUser user = apeUserService.getById(apeForum.getUserId());
            if (user != null) {
                apeForum.setAvatar(user.getAvatar());
                apeForum.setUserName(user.getUserName());
            }

            // 获取当前用户是否点赞
            String userId = ShiroUtils.getUserInfo().getId();
            QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.lambda().eq(ApeForumLike::getTargetId, id)
                    .eq(ApeForumLike::getUserId, userId)
                    .eq(ApeForumLike::getType, 1);
            long likeCount = apeForumLikeService.count(likeWrapper);

            // 获取总点赞数
            QueryWrapper<ApeForumLike> totalWrapper = new QueryWrapper<>();
            totalWrapper.lambda().eq(ApeForumLike::getTargetId, id).eq(ApeForumLike::getType, 1);
            int totalLikes = apeForumLikeService.count(totalWrapper);

            apeForum.setLikeCount(totalLikes);
            apeForum.setIsLiked(likeCount > 0);
        }
        return Result.success(apeForum);
    }

    /** 保存论坛 */
    @Log(name = "保存论坛", type = BusinessType.INSERT)
    @PostMapping("saveApeForum")
    public Result saveApeForum(@RequestBody ApeForum apeForum) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeForum.setUserId(userInfo.getId());
        apeForum.setCreateBy(userInfo.getUserName());
        boolean save = apeForumService.save(apeForum);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑论坛 */
    @Log(name = "编辑论坛", type = BusinessType.UPDATE)
    @PostMapping("editApeForum")
    public Result editApeForum(@RequestBody ApeForum apeForum) {
        boolean save = apeForumService.updateById(apeForum);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除论坛 */
    @GetMapping("removeApeForum")
    @Log(name = "删除论坛", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeForum(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeForumService.removeById(id);
                // 删除相关的评论
                QueryWrapper<ApeForumItem> itemWrapper = new QueryWrapper<>();
                itemWrapper.lambda().eq(ApeForumItem::getForumId, id);
                apeForumItemService.remove(itemWrapper);
                // 删除相关的点赞
                QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
                likeWrapper.lambda().eq(ApeForumLike::getTargetId, id);
                apeForumLikeService.remove(likeWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("论坛id不能为空！");
        }
    }

    // ==================== 点赞相关 ====================

    /** 点赞/取消点赞 */
    @Log(name = "点赞", type = BusinessType.OTHER)
    @PostMapping("like")
    public Result like(@RequestBody ApeForumLike apeForumLike) {
        String userId = ShiroUtils.getUserInfo().getId();
        String userName = ShiroUtils.getUserInfo().getUserName();

        // 获取 targetId（兼容前端传 id 或 targetId）
        String targetId = apeForumLike.getTargetId();
        if (targetId == null && apeForumLike.getId() != null) {
            targetId = apeForumLike.getId();
        }

        // 查询是否已点赞
        QueryWrapper<ApeForumLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeForumLike::getTargetId, targetId)
                .eq(ApeForumLike::getUserId, userId)
                .eq(ApeForumLike::getType, apeForumLike.getType());
        ApeForumLike exist = apeForumLikeService.getOne(queryWrapper);

        if (exist == null) {
            // 新增点赞
            ApeForumLike like = new ApeForumLike();
            like.setId(IdWorker.get32UUID());
            like.setTargetId(targetId);
            like.setUserId(userId);
            like.setType(apeForumLike.getType());
            like.setCreateBy(userName);
            boolean save = apeForumLikeService.save(like);
            if (save) {
                // 返回当前点赞总数
                QueryWrapper<ApeForumLike> countWrapper = new QueryWrapper<>();
                countWrapper.lambda().eq(ApeForumLike::getTargetId, targetId)
                        .eq(ApeForumLike::getType, apeForumLike.getType());
                int total = apeForumLikeService.count(countWrapper);
                return Result.success(total);
            } else {
                return Result.fail("点赞失败");
            }
        } else {
            // 取消点赞
            boolean remove = apeForumLikeService.removeById(exist.getId());
            if (remove) {
                // 返回当前点赞总数
                QueryWrapper<ApeForumLike> countWrapper = new QueryWrapper<>();
                countWrapper.lambda().eq(ApeForumLike::getTargetId, targetId)
                        .eq(ApeForumLike::getType, apeForumLike.getType());
                int total = apeForumLikeService.count(countWrapper);
                return Result.success(total);
            } else {
                return Result.fail("取消点赞失败");
            }
        }
    }

    // ==================== 内部类 ====================

    /**
     * 热门帖子VO类
     */
    static class HotForumVO {
        private String id;
        private String name;
        private String content;
        private String createBy;
        private Date createTime;  // 改为 Date 类型
        private String userId;
        private String avatar;
        private String userName;
        private Integer likeCount;
        private Integer commentCount;
        private Integer hotScore;

        // getter/setter
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCreateBy() { return createBy; }
        public void setCreateBy(String createBy) { this.createBy = createBy; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }  // 改为 Date
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public Integer getCommentCount() { return commentCount; }
        public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
        public Integer getHotScore() { return hotScore; }
        public void setHotScore(Integer hotScore) { this.hotScore = hotScore; }
    }
}