package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface ApeUserRoleService extends IService<ApeUserRole> {

    Set<String> getUserRolesSet(String loginAccount);

}
