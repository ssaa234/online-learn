package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeGenTableColumn;
import com.ape.apesystem.service.ApeGenTableColumnService;
import com.ape.apesystem.mapper.ApeGenTableColumnMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApeGenTableColumnServiceImpl extends ServiceImpl<ApeGenTableColumnMapper, ApeGenTableColumn> implements ApeGenTableColumnService {

    /**
    * 获取表字段
    */
    @Override
    public List<Map<String, Object>> getTableField(String table) {
        return baseMapper.getTableField(table);
    }
}
