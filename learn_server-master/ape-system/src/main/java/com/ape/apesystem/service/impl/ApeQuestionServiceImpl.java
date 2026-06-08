package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeQuestion;
import com.ape.apesystem.mapper.ApeQuestionMapper;
import com.ape.apesystem.service.ApeQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeQuestionServiceImpl extends ServiceImpl<ApeQuestionMapper, ApeQuestion> implements ApeQuestionService {
}