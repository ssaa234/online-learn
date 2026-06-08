package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeTest;
import com.ape.apesystem.service.ApeTestService;
import com.ape.apesystem.mapper.ApeTestMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApeTestServiceImpl extends ServiceImpl<ApeTestMapper, ApeTest> implements ApeTestService {
    @Override
    public Map<String,Object> getStudentTotalScore(String taskId, String userId) {
        return baseMapper.getStudentTotalScore(taskId,userId);
    }
}