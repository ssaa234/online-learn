package com.ape.apesystem.mapper;

import com.ape.apesystem.domain.ApeMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApeMenuMapper extends BaseMapper<ApeMenu> {

    List<ApeMenu> getMenuByUser(@Param("id") String id);

}
