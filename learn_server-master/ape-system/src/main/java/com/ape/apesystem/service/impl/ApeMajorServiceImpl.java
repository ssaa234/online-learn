package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeMajor;
import com.ape.apesystem.service.ApeMajorService;
import com.ape.apesystem.mapper.ApeMajorMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeMajorServiceImpl extends ServiceImpl<ApeMajorMapper, ApeMajor> implements ApeMajorService {
}