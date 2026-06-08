package com.ape.apeadmin.controller.forum;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeForumItem;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeForumLike;
import com.ape.apesystem.service.ApeForumItemService;
import com.ape.apesystem.service.ApeUserService;
import com.ape.apesystem.service.ApeForumLikeService;
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
@RequestMapping("item")
public class ApeForumItemController {

    @Autowired
    private ApeForumItemService apeForumItemService;

    @Autowired
    private ApeUserService apeUserService;

    @Autowired
    private ApeForumLikeService apeForumLikeService;

    /** 分页获取论坛讨论 */
    @Log(name = "分页获取论坛讨论", type = BusinessType.OTHER)
    @PostMapping("getApeForumItemPage")
    public Result getApeForumItemPage(@RequestBody ApeForumItem apeForumItem) {
        Page<ApeForumItem> page = new Page<>(apeForumItem.getPageNumber(), apeForumItem.getPageSize());
        QueryWrapper<ApeForumItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeForumItem.getForumId()), ApeForumItem::getForumId, apeForumItem.getForumId())
                .orderByDesc(ApeForumItem::getCreateTime);
        Page<ApeForumItem> apeForumItemPage = apeForumItemService.page(page, queryWrapper);

        // 补充用户信息
        for (ApeForumItem item : apeForumItemPage.getRecords()) {
            if (StringUtils.isNotBlank(item.getUserId())) {
                ApeUser user = apeUserService.getById(item.getUserId());
                if (user != null) {
                    item.setUserName(user.getUserName());
                    item.setUserAvatar(user.getAvatar());
                }
            }
        }

        return Result.success(apeForumItemPage);
    }

    /** 获取论坛评论列表（平铺列表，不分层）- 完全参照课程评论逻辑 */
    @PostMapping("getApeForumItemList")
    public Result getApeForumItemList(@RequestBody ApeForumItem apeForumItem) {
        // 查询所有评论，按创建时间倒序（最新在前）
        QueryWrapper<ApeForumItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeForumItem::getForumId, apeForumItem.getForumId())
                .orderByDesc(ApeForumItem::getCreateTime);
        List<ApeForumItem> commentList = apeForumItemService.list(queryWrapper);

        // 获取当前用户ID
        String userId = ShiroUtils.getUserInfo().getId();

        // 为每个评论添加点赞数、点赞状态和用户头像（完全参照课程评论）
        for (ApeForumItem comment : commentList) {
            // 统计点赞数（type=2 表示评论点赞）
            QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.lambda().eq(ApeForumLike::getTargetId, comment.getId()).eq(ApeForumLike::getType, 2);
            int likeCount = (int) apeForumLikeService.count(likeWrapper);
            comment.setLikeCount(likeCount);

            // 判断当前用户是否点赞
            QueryWrapper<ApeForumLike> userLikeWrapper = new QueryWrapper<>();
            userLikeWrapper.lambda()
                    .eq(ApeForumLike::getTargetId, comment.getId())
                    .eq(ApeForumLike::getUserId, userId)
                    .eq(ApeForumLike::getType, 2);
            boolean isLiked = apeForumLikeService.count(userLikeWrapper) > 0;
            comment.setIsLiked(isLiked);

            // 通过 userId 获取用户头像和用户名
            if (StringUtils.isNotBlank(comment.getUserId())) {
                ApeUser user = apeUserService.getById(comment.getUserId());
                if (user != null) {
                    comment.setUserAvatar(user.getAvatar());
                    // 如果没有 userName，使用用户名
                    if (StringUtils.isBlank(comment.getUserName())) {
                        comment.setUserName(user.getUserName());
                    }
                }
            }
        }
        return Result.success(commentList);
    }

    /** 根据id获取论坛讨论 */
    @GetMapping("getApeForumItemById")
    public Result getApeForumItemById(@RequestParam("id")String id) {
        ApeForumItem apeForumItem = apeForumItemService.getById(id);
        // 补充用户信息
        if (apeForumItem != null && StringUtils.isNotBlank(apeForumItem.getUserId())) {
            ApeUser user = apeUserService.getById(apeForumItem.getUserId());
            if (user != null) {
                apeForumItem.setUserName(user.getUserName());
                apeForumItem.setUserAvatar(user.getAvatar());
            }
        }
        return Result.success(apeForumItem);
    }

    /** 保存论坛评论（支持回复）- 完全参照课程评论逻辑 */
    @PostMapping("saveApeForumItem")
    public Result saveApeForumItem(@RequestBody ApeForumItem apeForumItem) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeForumItem.setId(IdWorker.get32UUID());
        apeForumItem.setUserId(userInfo.getId());
        apeForumItem.setCreateBy(userInfo.getUserName());
        // replyTo 字段会自动保存（前端传入或后端设置）

        boolean save = apeForumItemService.save(apeForumItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑论坛讨论 */
    @Log(name = "编辑论坛讨论", type = BusinessType.UPDATE)
    @PostMapping("editApeForumItem")
    public Result editApeForumItem(@RequestBody ApeForumItem apeForumItem) {
        boolean save = apeForumItemService.updateById(apeForumItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除论坛讨论 */
    @GetMapping("removeApeForumItem")
    @Log(name = "删除论坛讨论", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeForumItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeForumItemService.removeById(id);
                // 删除该评论的点赞记录
                QueryWrapper<ApeForumLike> likeWrapper = new QueryWrapper<>();
                likeWrapper.lambda().eq(ApeForumLike::getTargetId, id);
                apeForumLikeService.remove(likeWrapper);
                // 删除子评论
                QueryWrapper<ApeForumItem> childWrapper = new QueryWrapper<>();
                childWrapper.lambda().eq(ApeForumItem::getParentId, id);
                apeForumItemService.remove(childWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("论坛讨论id不能为空！");
        }
    }

    // ==================== 点赞相关 ====================

    /** 点赞/取消点赞评论 */
    @Log(name = "点赞评论", type = BusinessType.OTHER)
    @PostMapping("like")
    public Result likeComment(@RequestBody ApeForumLike like) {
        String userId = ShiroUtils.getUserInfo().getId();
        String targetId = like.getTargetId();
        Integer type = like.getType(); // type=1 帖子点赞，type=2 评论点赞

        // 查询是否已点赞
        QueryWrapper<ApeForumLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeForumLike::getTargetId, targetId)
                .eq(ApeForumLike::getUserId, userId)
                .eq(ApeForumLike::getType, type);
        ApeForumLike exist = apeForumLikeService.getOne(queryWrapper);

        if (exist == null) {
            // 新增点赞
            ApeForumLike newLike = new ApeForumLike();
            newLike.setId(IdWorker.get32UUID());
            newLike.setTargetId(targetId);
            newLike.setUserId(userId);
            newLike.setType(type);
            boolean save = apeForumLikeService.save(newLike);
            if (save) {
                // 返回当前点赞总数
                QueryWrapper<ApeForumLike> countWrapper = new QueryWrapper<>();
                countWrapper.lambda().eq(ApeForumLike::getTargetId, targetId).eq(ApeForumLike::getType, type);
                int total = (int) apeForumLikeService.count(countWrapper);
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
                countWrapper.lambda().eq(ApeForumLike::getTargetId, targetId).eq(ApeForumLike::getType, type);
                int total = (int) apeForumLikeService.count(countWrapper);
                return Result.success(total);
            } else {
                return Result.fail("取消点赞失败");
            }
        }
    }
}