package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeTaskStudent;
import com.ape.apesystem.service.ApeTaskStudentService;
import com.ape.apesystem.mapper.ApeTaskStudentMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeTaskStudentServiceImpl extends ServiceImpl<ApeTaskStudentMapper, ApeTaskStudent> implements ApeTaskStudentService {
}