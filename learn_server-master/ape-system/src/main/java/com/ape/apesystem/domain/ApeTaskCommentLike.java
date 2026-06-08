package com.ape.apesystem.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ape_task_comment_like")
public class ApeTaskCommentLike {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 评论ID */
    private String commentId;

    /** 用户ID */
    private String userId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    // 添加以下字段，标记为不存在，防止自动填充报错
    @TableField(exist = false)
    private Date updateTime;

    @TableField(exist = false)
    private String createBy;

    @TableField(exist = false)
    private String updateBy;
}