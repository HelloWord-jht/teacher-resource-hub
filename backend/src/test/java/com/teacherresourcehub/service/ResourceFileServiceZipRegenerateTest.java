package com.teacherresourcehub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.entity.ImportTask;
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
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ResourceFileServiceZipRegenerateTest {

    @TempDir
    Path tempDir;

    @Test
    void regeneratingZipPreviewShouldNotDuplicateExtractedChildren() throws Exception {
        ResourceMapper resourceMapper = Mockito.mock(ResourceMapper.class);
        ImportTaskMapper importTaskMapper = Mockito.mock(ImportTaskMapper.class);
        ResourceFileMapper resourceFileMapper = Mockito.mock(ResourceFileMapper.class);
        ResourceFilePreviewMapper previewMapper = Mockito.mock(ResourceFilePreviewMapper.class);
        ResourceFileProcessLogMapper logMapper = Mockito.mock(ResourceFileProcessLogMapper.class);

        ImportTask importTask = new ImportTask();
        importTask.setId(1L);
        importTask.setImportStatus("pending");
        when(importTaskMapper.selectById(1L)).thenReturn(importTask);

        List<ResourceFile> storedFiles = new ArrayList<>();
        AtomicLong fileIds = new AtomicLong(1L);
        doAnswer(invocation -> {
            ResourceFile file = invocation.getArgument(0);
            if (file.getId() == null) {
                file.setId(fileIds.getAndIncrement());
                storedFiles.add(file);
            }
            return 1;
        }).when(resourceFileMapper).insert(any(ResourceFile.class));

        doAnswer(invocation -> {
            ResourceFile updatedFile = invocation.getArgument(0);
            for (int index = 0; index < storedFiles.size(); index++) {
                if (storedFiles.get(index).getId().equals(updatedFile.getId())) {
                    storedFiles.set(index, updatedFile);
                    return 1;
                }
            }
            return 1;
        }).when(resourceFileMapper).updateById(any(ResourceFile.class));

        when(resourceFileMapper.selectById(any())).thenAnswer(invocation ->
                storedFiles.stream()
                        .filter(file -> file.getId().equals(invocation.getArgument(0)))
                        .findFirst()
                        .orElse(null)
        );
        AtomicInteger selectListCount = new AtomicInteger();
        when(resourceFileMapper.selectList(any(LambdaQueryWrapper.class))).thenAnswer(invocation -> {
            int call = selectListCount.incrementAndGet();
            if (call == 1 || call == 3) {
                return storedFiles.stream()
                        .filter(file -> file.getParentZipFileId() != null)
                        .toList();
            }
            return new ArrayList<>(storedFiles);
        });
        doAnswer(invocation -> {
            Long fileId = invocation.getArgument(0);
            storedFiles.removeIf(file -> file.getId().equals(fileId));
            return 1;
        }).when(resourceFileMapper).deleteById(Mockito.any(Long.class));

        List<ResourceFilePreview> previews = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFilePreview preview = invocation.getArgument(0);
            previews.add(preview);
            return 1;
        }).when(previewMapper).insert(any(ResourceFilePreview.class));
        doAnswer(invocation -> {
            previews.clear();
            return 1;
        }).when(previewMapper).delete(any());
        when(previewMapper.selectList(any())).thenAnswer(invocation -> new ArrayList<>(previews));

        List<ResourceFileProcessLog> logs = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFileProcessLog log = invocation.getArgument(0);
            logs.add(log);
            return 1;
        }).when(logMapper).insert(any(ResourceFileProcessLog.class));
        when(logMapper.selectList(any())).thenAnswer(invocation -> new ArrayList<>(logs));

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

        MockMultipartFile zipFile = new MockMultipartFile(
                "file",
                "sample.zip",
                "application/zip",
                createZipWithTextFile()
        );

        service.uploadImportTaskFile(1L, zipFile);

        long childCountAfterUpload = storedFiles.stream()
                .filter(file -> file.getParentZipFileId() != null)
                .count();
        Long zipFileId = storedFiles.stream()
                .filter(file -> "zip".equals(file.getDetectedType()))
                .map(ResourceFile::getId)
                .findFirst()
                .orElseThrow();

        service.regeneratePreview(zipFileId);

        long childCountAfterRegenerate = storedFiles.stream()
                .filter(file -> file.getParentZipFileId() != null)
                .count();

        assertThat(childCountAfterUpload).isEqualTo(1);
        assertThat(childCountAfterRegenerate).isEqualTo(1);
        assertThat(importTask.getImportStatus()).isEqualTo("success");
    }

    private byte[] createZipWithTextFile() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            zipOutputStream.putNextEntry(new ZipEntry("lesson/readme.txt"));
            zipOutputStream.write("这是一个班会资料样张".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }
}
