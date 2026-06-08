package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface ApeRoleMenuService extends IService<ApeRoleMenu> {
    Set<String> getRoleMenusSet(String role);
}
