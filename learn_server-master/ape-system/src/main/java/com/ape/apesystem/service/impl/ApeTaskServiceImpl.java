package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeTask;
import com.ape.apesystem.service.ApeTaskService;
import com.ape.apesystem.mapper.ApeTaskMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeTaskServiceImpl extends ServiceImpl<ApeTaskMapper, ApeTask> implements ApeTaskService {
}