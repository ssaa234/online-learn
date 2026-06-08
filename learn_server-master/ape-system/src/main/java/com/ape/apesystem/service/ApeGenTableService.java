package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeGenTable;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ApeGenTableService extends IService<ApeGenTable> {
    Page<Map<String,Object>> getTables(ApeGenTable apeGenTable);
    Map<String, Object> getTablesInfo(String table);
}
