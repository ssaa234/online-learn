package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeGenTableColumn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ApeGenTableColumnService extends IService<ApeGenTableColumn> {
    List<Map<String, Object>> getTableField(String table);
}
