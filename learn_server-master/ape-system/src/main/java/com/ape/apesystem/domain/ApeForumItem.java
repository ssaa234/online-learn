package com.ape.apesystem.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ape_forum_item")
public class ApeForumItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 论坛id
     */
    private String forumId;

    /**
     * 内容
     */
    private String content;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 父评论ID（NULL表示顶级评论）
     */
    private String parentId;

    /**
     * 被回复的用户名
     */
    private String replyTo;

    // ========== 非数据库字段 ==========

    /** 用户名（通过 userId 关联查询） */
    @TableField(exist = false)
    private String userName;

    /** 用户头像（通过 userId 关联查询） */
    @TableField(exist = false)
    private String userAvatar;

    /** 点赞数 */
    @TableField(exist = false)
    private Integer likeCount;

    /** 是否点赞 */
    @TableField(exist = false)
    private Boolean isLiked;

    /** 回复列表（第二层） */
    @TableField(exist = false)
    private List<ApeForumItem> replies;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;
}