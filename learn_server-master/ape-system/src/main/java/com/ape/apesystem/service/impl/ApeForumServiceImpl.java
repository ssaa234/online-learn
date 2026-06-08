package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeForum;
import com.ape.apesystem.service.ApeForumService;
import com.ape.apesystem.mapper.ApeForumMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeForumServiceImpl extends ServiceImpl<ApeForumMapper, ApeForum> implements ApeForumService {
}