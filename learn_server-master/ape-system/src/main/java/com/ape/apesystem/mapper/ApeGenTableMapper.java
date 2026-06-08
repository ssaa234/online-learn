package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeGenTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ApeGenTableMapper extends BaseMapper<ApeGenTable> {
    Page<Map<String, Object>> getTables(Page<Map<String, Object>> page, @Param("ew") ApeGenTable apeGenTable,@Param("tables") List<String> tables);
    Map<String, Object> getTablesInfo(@Param("tableName") String table);
}
