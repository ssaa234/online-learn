package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeGenTableColumn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ApeGenTableColumnMapper extends BaseMapper<ApeGenTableColumn> {
    List<Map<String, Object>> getTableField(@Param("table") String table);
}
