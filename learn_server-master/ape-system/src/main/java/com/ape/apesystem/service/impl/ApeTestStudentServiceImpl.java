package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeTestStudent;
import com.ape.apesystem.service.ApeTestStudentService;
import com.ape.apesystem.mapper.ApeTestStudentMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeTestStudentServiceImpl extends ServiceImpl<ApeTestStudentMapper, ApeTestStudent> implements ApeTestStudentService {
}