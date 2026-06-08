package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface ApeRoleMenuMapper extends BaseMapper<ApeRoleMenu> {
    Set<String> getRoleMenusSet(@Param("role") String role);
}
