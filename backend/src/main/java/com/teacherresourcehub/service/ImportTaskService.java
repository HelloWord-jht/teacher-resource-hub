package com.teacherresourcehub.service;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.dto.ImportTaskSaveRequest;
import com.teacherresourcehub.vo.ImportTaskVO;

public interface ImportTaskService {

    PageResult<ImportTaskVO> page(String importStatus, Long pageNum, Long pageSize);

    ImportTaskVO getById(Long id);

    void create(ImportTaskSaveRequest request);

    void execute(Long id);

    java.util.List<ImportTaskVO> listRecent(int limit);
}
