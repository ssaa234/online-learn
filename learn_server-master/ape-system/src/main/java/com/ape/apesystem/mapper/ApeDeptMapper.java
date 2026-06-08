package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface ApeDeptMapper extends BaseMapper<ApeDept> {
    List<Map<String, Object>> getDeptDrop();
    List<Map<String, Object>> getDeptDropFirst();
}
