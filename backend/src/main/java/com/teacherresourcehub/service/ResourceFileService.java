package com.teacherresourcehub.service;

import com.teacherresourcehub.vo.AdminResourceFileDetailVO;
import com.teacherresourcehub.vo.AdminResourceFileItemVO;
import com.teacherresourcehub.vo.AdminResourceFileLogVO;
import com.teacherresourcehub.vo.AdminResourceFilePreviewVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceFileService {

    void uploadResourceFile(Long resourceId, MultipartFile file);

    void uploadImportTaskFile(Long importTaskId, MultipartFile file);

    List<AdminResourceFileItemVO> listResourceFiles(Long resourceId);

    List<AdminResourceFileItemVO> listImportTaskFiles(Long importTaskId);

    AdminResourceFileDetailVO getResourceFileDetail(Long fileId);

    List<AdminResourceFilePreviewVO> listFilePreviews(Long fileId);

    List<AdminResourceFileLogVO> listFileLogs(Long fileId);

    void regeneratePreview(Long fileId);

    void setPrimaryFile(Long resourceId, Long fileId);

    void deleteFile(Long fileId);

    void executeImportTaskPreview(Long importTaskId);

    void bindImportTaskFilesToResource(Long importTaskId, Long resourceId);
}
