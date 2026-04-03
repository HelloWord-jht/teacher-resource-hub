package com.teacherresourcehub.service;

import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourceFile;
import com.teacherresourcehub.entity.ResourceFilePreview;
import com.teacherresourcehub.entity.ResourceFileProcessLog;
import com.teacherresourcehub.mapper.ImportTaskMapper;
import com.teacherresourcehub.mapper.ResourceFileMapper;
import com.teacherresourcehub.mapper.ResourceFilePreviewMapper;
import com.teacherresourcehub.mapper.ResourceFileProcessLogMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.service.impl.ResourceFileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ResourceFileServiceBindImportTaskFilesTest {

    @TempDir
    Path tempDir;

    @Test
    void bindImportTaskFilesShouldAttachFilesAndRefreshPrimaryPreviewState() {
        ResourceMapper resourceMapper = Mockito.mock(ResourceMapper.class);
        ImportTaskMapper importTaskMapper = Mockito.mock(ImportTaskMapper.class);
        ResourceFileMapper resourceFileMapper = Mockito.mock(ResourceFileMapper.class);
        ResourceFilePreviewMapper previewMapper = Mockito.mock(ResourceFilePreviewMapper.class);
        ResourceFileProcessLogMapper logMapper = Mockito.mock(ResourceFileProcessLogMapper.class);

        Resource resource = new Resource();
        resource.setId(5L);
        when(resourceMapper.selectById(5L)).thenReturn(resource);

        ResourceFile pdfFile = new ResourceFile();
        pdfFile.setId(11L);
        pdfFile.setImportTaskId(77L);
        pdfFile.setResourceId(null);
        pdfFile.setDetectedType("pdf");
        pdfFile.setPreviewStatus("success");
        pdfFile.setPreviewPageCount(2);
        pdfFile.setSortOrder(100);
        pdfFile.setIsPrimary(0);

        ResourceFile unsupportedFile = new ResourceFile();
        unsupportedFile.setId(12L);
        unsupportedFile.setImportTaskId(77L);
        unsupportedFile.setResourceId(null);
        unsupportedFile.setDetectedType("xlsx");
        unsupportedFile.setPreviewStatus("unsupported");
        unsupportedFile.setPreviewPageCount(0);
        unsupportedFile.setSortOrder(90);
        unsupportedFile.setIsPrimary(0);

        List<ResourceFile> files = new ArrayList<>(List.of(pdfFile, unsupportedFile));
        AtomicInteger selectListCount = new AtomicInteger();
        when(resourceFileMapper.selectList(any())).thenAnswer(invocation -> {
            int callIndex = selectListCount.incrementAndGet();
            if (callIndex == 1) {
                return files;
            }
            return files;
        });

        doAnswer(invocation -> {
            ResourceFile updatedFile = invocation.getArgument(0);
            for (ResourceFile file : files) {
                if (file.getId().equals(updatedFile.getId())) {
                    file.setResourceId(updatedFile.getResourceId());
                    file.setIsPrimary(updatedFile.getIsPrimary());
                    file.setPreviewStatus(updatedFile.getPreviewStatus());
                    file.setPreviewPageCount(updatedFile.getPreviewPageCount());
                }
            }
            return 1;
        }).when(resourceFileMapper).updateById(any(ResourceFile.class));

        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);

        ResourceFileService service = new ResourceFileServiceImpl(
                resourceMapper,
                importTaskMapper,
                resourceFileMapper,
                previewMapper,
                logMapper,
                new com.teacherresourcehub.common.util.FileTypeDetector(),
                new com.teacherresourcehub.common.util.SafeZipExtractor(),
                (sourceFile, outputDirectory) -> sourceFile,
                tempDir,
                "/preview-files"
        );

        service.bindImportTaskFilesToResource(77L, 5L);

        assertThat(pdfFile.getResourceId()).isEqualTo(5L);
        assertThat(unsupportedFile.getResourceId()).isEqualTo(5L);
        assertThat(pdfFile.getIsPrimary()).isEqualTo(1);

        Mockito.verify(resourceMapper).updateById(resourceCaptor.capture());
        Resource updatedResource = resourceCaptor.getValue();
        assertThat(updatedResource.getPrimaryFileId()).isEqualTo(11L);
        assertThat(updatedResource.getPreviewAvailableStatus()).isEqualTo("partial");
        assertThat(updatedResource.getPreviewMode()).isEqualTo("single");
        assertThat(updatedResource.getPreviewCount()).isEqualTo(2);
    }
}
