package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeSchool;
import com.ape.apesystem.service.ApeSchoolService;
import com.ape.apesystem.mapper.ApeSchoolMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeSchoolServiceImpl extends ServiceImpl<ApeSchoolMapper, ApeSchool> implements ApeSchoolService {
}