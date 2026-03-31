package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.PageContentSaveRequest;
import com.teacherresourcehub.vo.PageContentVO;

import java.util.List;

public interface PageContentService {

    List<PageContentVO> listAll();

    PageContentVO getByCode(String pageCode);

    void saveOrUpdate(String pageCode, PageContentSaveRequest request);
}
