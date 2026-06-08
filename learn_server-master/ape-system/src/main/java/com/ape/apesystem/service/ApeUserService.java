package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ApeUserService extends IService<ApeUser> {
    Page<ApeUser> getUserPage(ApeUser apeUser);
}
