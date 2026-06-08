package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeRoleMenu;
import com.ape.apesystem.service.ApeRoleMenuService;
import com.ape.apesystem.mapper.ApeRoleMenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ApeRoleMenuServiceImpl extends ServiceImpl<ApeRoleMenuMapper, ApeRoleMenu> implements ApeRoleMenuService {

    /**
    *  根据角色获取权限
    */
    @Override
    public Set<String> getRoleMenusSet(String role) {
        return baseMapper.getRoleMenusSet(role);
    }
}
