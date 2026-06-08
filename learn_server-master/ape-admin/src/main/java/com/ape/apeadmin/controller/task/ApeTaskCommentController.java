package com.ape.apeadmin.controller.task;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeTaskComment;
import com.ape.apesystem.domain.ApeTaskCommentLike;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeTaskCommentLikeService;
import com.ape.apesystem.service.ApeTaskCommentService;
import com.ape.apesystem.service.ApeUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("comment")
public class ApeTaskCommentController {

    @Autowired
    private ApeTaskCommentService apeTaskCommentService;

    @Autowired
    private ApeTaskCommentLikeService apeTaskCommentLikeService;

    @Autowired
    private ApeUserService apeUserService;  // 新增用户服务

    /** 分页获取课程评论 */
    @Log(name = "分页获取课程评论", type = BusinessType.OTHER)
    @PostMapping("getApeTaskCommentPage")
    public Result getApeTaskCommentPage(@RequestBody ApeTaskComment apeTaskComment) {
        Page<ApeTaskComment> page = new Page<>(apeTaskComment.getPageNumber(), apeTaskComment.getPageSize());
        QueryWrapper<ApeTaskComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeTaskComment.getTaskId()), ApeTaskComment::getTaskId, apeTaskComment.getTaskId())
                .like(StringUtils.isNotBlank(apeTaskComment.getContent()), ApeTaskComment::getContent, apeTaskComment.getContent())
                .like(StringUtils.isNotBlank(apeTaskComment.getCreateBy()), ApeTaskComment::getCreateBy, apeTaskComment.getCreateBy());
        Page<ApeTaskComment> apeTaskCommentPage = apeTaskCommentService.page(page, queryWrapper);
        return Result.success(apeTaskCommentPage);
    }

    /** 根据id获取课程评论 */
    @Log(name = "根据id获取课程评论", type = BusinessType.OTHER)
    @GetMapping("getApeTaskCommentById")
    public Result getApeTaskCommentById(@RequestParam("id")String id) {
        ApeTaskComment apeTaskComment = apeTaskCommentService.getById(id);
        return Result.success(apeTaskComment);
    }

    /** 获取课程评论列表（含点赞状态和用户头像） */
    @GetMapping("getApeTaskCommentListByTaskId")
    public Result getApeTaskCommentListByTaskId(@RequestParam("id")String id) {
        QueryWrapper<ApeTaskComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTaskComment::getTaskId, id).orderByDesc(ApeTaskComment::getCreateTime);
        List<ApeTaskComment> commentList = apeTaskCommentService.list(queryWrapper);

        // 获取当前用户ID
        String userId = ShiroUtils.getUserInfo().getId();

        // 为每个评论添加点赞数、点赞状态和用户头像
        for (ApeTaskComment comment : commentList) {
            // 统计点赞数
            QueryWrapper<ApeTaskCommentLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.lambda().eq(ApeTaskCommentLike::getCommentId, comment.getId());
            int likeCount = apeTaskCommentLikeService.count(likeWrapper);
            comment.setLikeCount(likeCount);

            // 判断当前用户是否点赞
            QueryWrapper<ApeTaskCommentLike> userLikeWrapper = new QueryWrapper<>();
            userLikeWrapper.lambda()
                    .eq(ApeTaskCommentLike::getCommentId, comment.getId())
                    .eq(ApeTaskCommentLike::getUserId, userId);
            boolean isLiked = apeTaskCommentLikeService.count(userLikeWrapper) > 0;
            comment.setIsLiked(isLiked);

            // 通过 userId 获取用户头像
            if (StringUtils.isNotBlank(comment.getUserId())) {
                ApeUser user = apeUserService.getById(comment.getUserId());
                if (user != null) {
                    comment.setAvatar(user.getAvatar());
                    // 如果没有 createBy，使用用户名
                    if (StringUtils.isBlank(comment.getCreateBy())) {
                        comment.setCreateBy(user.getUserName());
                    }
                }
            }
        }
        return Result.success(commentList);
    }

    /** 保存课程评论 */
    @PostMapping("saveApeTaskComment")
    public Result saveApeTaskComment(@RequestBody ApeTaskComment apeTaskComment) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeTaskComment.setId(IdWorker.get32UUID());
        apeTaskComment.setUserId(userInfo.getId());
        apeTaskComment.setCreateBy(userInfo.getUserName());
        // replyTo 字段会自动保存
        boolean save = apeTaskCommentService.save(apeTaskComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑课程评论 */
    @Log(name = "编辑课程评论", type = BusinessType.UPDATE)
    @PostMapping("editApeTaskComment")
    public Result editApeTaskComment(@RequestBody ApeTaskComment apeTaskComment) {
        boolean save = apeTaskCommentService.updateById(apeTaskComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除课程评论 */
    @GetMapping("removeApeTaskComment")
    @Log(name = "删除课程评论", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeTaskComment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeTaskCommentService.removeById(id);
                // 删除评论时同时删除点赞记录
                QueryWrapper<ApeTaskCommentLike> likeWrapper = new QueryWrapper<>();
                likeWrapper.lambda().eq(ApeTaskCommentLike::getCommentId, id);
                apeTaskCommentLikeService.remove(likeWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("课程评论id不能为空！");
        }
    }

    // ==================== 点赞相关 ====================

    /** 点赞/取消点赞评论 */
    @Log(name = "点赞评论", type = BusinessType.OTHER)
    @PostMapping("like")
    public Result likeComment(@RequestBody ApeTaskCommentLike like) {
        String userId = ShiroUtils.getUserInfo().getId();
        String commentId = like.getCommentId();

        // 查询是否已点赞
        QueryWrapper<ApeTaskCommentLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeTaskCommentLike::getCommentId, commentId)
                .eq(ApeTaskCommentLike::getUserId, userId);
        ApeTaskCommentLike exist = apeTaskCommentLikeService.getOne(queryWrapper);

        if (exist == null) {
            // 新增点赞
            ApeTaskCommentLike newLike = new ApeTaskCommentLike();
            newLike.setId(IdWorker.get32UUID());
            newLike.setCommentId(commentId);
            newLike.setUserId(userId);
            boolean save = apeTaskCommentLikeService.save(newLike);
            if (save) {
                // 返回当前点赞总数
                QueryWrapper<ApeTaskCommentLike> countWrapper = new QueryWrapper<>();
                countWrapper.lambda().eq(ApeTaskCommentLike::getCommentId, commentId);
                int total = apeTaskCommentLikeService.count(countWrapper);
                return Result.success(total);
            } else {
                return Result.fail("点赞失败");
            }
        } else {
            // 取消点赞
            boolean remove = apeTaskCommentLikeService.removeById(exist.getId());
            if (remove) {
                // 返回当前点赞总数
                QueryWrapper<ApeTaskCommentLike> countWrapper = new QueryWrapper<>();
                countWrapper.lambda().eq(ApeTaskCommentLike::getCommentId, commentId);
                int total = apeTaskCommentLikeService.count(countWrapper);
                return Result.success(total);
            } else {
                return Result.fail("取消点赞失败");
            }
        }
    }
}