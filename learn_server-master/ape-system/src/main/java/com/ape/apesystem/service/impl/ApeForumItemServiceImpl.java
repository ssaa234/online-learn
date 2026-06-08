package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeForumItem;
import com.ape.apesystem.service.ApeForumItemService;
import com.ape.apesystem.mapper.ApeForumItemMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApeForumItemServiceImpl extends ServiceImpl<ApeForumItemMapper, ApeForumItem> implements ApeForumItemService {
}