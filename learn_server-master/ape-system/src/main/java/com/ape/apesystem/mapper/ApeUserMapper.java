package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface ApeUserMapper extends BaseMapper<ApeUser> {
    Page<ApeUser> getUserPage(Page<ApeUser> page, @Param("ew")ApeUser apeUser);

}
