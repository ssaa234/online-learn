package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeLoginLog;
import com.ape.apesystem.service.ApeLoginLogService;
import com.ape.apesystem.mapper.ApeLoginLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeLoginLogServiceImpl extends ServiceImpl<ApeLoginLogMapper, ApeLoginLog> implements ApeLoginLogService {

}
