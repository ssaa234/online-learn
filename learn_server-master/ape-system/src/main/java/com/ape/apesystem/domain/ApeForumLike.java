package com.ape.apesystem.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ape_forum_like")
public class ApeForumLike {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String targetId;
    private String userId;
    private Integer type;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    // 添加这个字段，但标记为不存在于数据库，防止自动填充报错
    @TableField(exist = false)
    private String updateBy;
}