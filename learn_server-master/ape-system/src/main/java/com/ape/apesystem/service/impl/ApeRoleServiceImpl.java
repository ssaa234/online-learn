package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeRole;
import com.ape.apesystem.service.ApeRoleService;
import com.ape.apesystem.mapper.ApeRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeRoleServiceImpl extends ServiceImpl<ApeRoleMapper, ApeRole> implements ApeRoleService {
}
