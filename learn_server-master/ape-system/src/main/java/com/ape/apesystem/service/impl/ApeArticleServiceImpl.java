package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeArticle;
import com.ape.apesystem.service.ApeArticleService;
import com.ape.apesystem.mapper.ApeArticleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeArticleServiceImpl extends ServiceImpl<ApeArticleMapper, ApeArticle> implements ApeArticleService {
}