package com.teacherresourcehub.service;

import com.teacherresourcehub.vo.WebResourcePreviewFileVO;
import com.teacherresourcehub.vo.WebResourcePreviewPageVO;

import java.util.List;

public interface ResourceFileQueryService {

    List<WebResourcePreviewFileVO> listWebPreviewFiles(Long resourceId);

    List<WebResourcePreviewPageVO> listWebPreviewPages(Long resourceId, Long fileId);
}
