package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeTestItem;
import com.ape.apesystem.service.ApeTestItemService;
import com.ape.apesystem.mapper.ApeTestItemMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeTestItemServiceImpl extends ServiceImpl<ApeTestItemMapper, ApeTestItem> implements ApeTestItemService {
}