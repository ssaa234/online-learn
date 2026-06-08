package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ApeMenuService extends IService<ApeMenu> {
    List<ApeMenu> getMenuByUser(String id);

}
