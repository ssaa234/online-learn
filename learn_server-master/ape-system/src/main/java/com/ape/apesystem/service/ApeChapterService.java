package com.ape.apesystem.service;

import com.ape.apesystem.domain.ApeChapter;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ApeChapterService extends IService<ApeChapter> {
    Integer getStudentVideo(String taskId, String userId);
}