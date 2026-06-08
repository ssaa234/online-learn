package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeTaskComment;
import com.ape.apesystem.service.ApeTaskCommentService;
import com.ape.apesystem.mapper.ApeTaskCommentMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeTaskCommentServiceImpl extends ServiceImpl<ApeTaskCommentMapper, ApeTaskComment> implements ApeTaskCommentService {
}