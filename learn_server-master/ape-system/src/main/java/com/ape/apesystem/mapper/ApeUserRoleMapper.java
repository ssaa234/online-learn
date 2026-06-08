package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;
public interface ApeUserRoleMapper extends BaseMapper<ApeUserRole> {

    /**
     * 根据账号获取角色
     */
    Set<String> getUserRolesSet(@Param("loginAccount") String loginAccount);
}
