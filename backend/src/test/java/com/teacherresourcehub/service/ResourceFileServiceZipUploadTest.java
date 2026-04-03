package com.teacherresourcehub.service;

import com.teacherresourcehub.entity.ImportTask;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourceFile;
import com.teacherresourcehub.entity.ResourceFilePreview;
import com.teacherresourcehub.mapper.ImportTaskMapper;
import com.teacherresourcehub.mapper.ResourceFileMapper;
import com.teacherresourcehub.mapper.ResourceFilePreviewMapper;
import com.teacherresourcehub.mapper.ResourceFileProcessLogMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.service.impl.ResourceFileServiceImpl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ResourceFileServiceZipUploadTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldUploadZipAndCreateChildPreviewableFileRecords() throws Exception {
        ResourceMapper resourceMapper = Mockito.mock(ResourceMapper.class);
        ImportTaskMapper importTaskMapper = Mockito.mock(ImportTaskMapper.class);
        ResourceFileMapper resourceFileMapper = Mockito.mock(ResourceFileMapper.class);
        ResourceFilePreviewMapper previewMapper = Mockito.mock(ResourceFilePreviewMapper.class);
        ResourceFileProcessLogMapper logMapper = Mockito.mock(ResourceFileProcessLogMapper.class);

        Resource resource = new Resource();
        resource.setId(5L);
        when(resourceMapper.selectById(5L)).thenReturn(resource);

        List<ResourceFile> insertedFiles = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFile file = invocation.getArgument(0);
            file.setId((long) (insertedFiles.size() + 1));
            insertedFiles.add(file);
            return 1;
        }).when(resourceFileMapper).insert(any(ResourceFile.class));

        List<ResourceFilePreview> insertedPreviews = new ArrayList<>();
        doAnswer(invocation -> {
            insertedPreviews.add(invocation.getArgument(0));
            return 1;
        }).when(previewMapper).insert(any(ResourceFilePreview.class));

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

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "bundle.zip",
                "application/zip",
                createZipBytes()
        );

        service.uploadResourceFile(5L, multipartFile);

        assertThat(insertedFiles).hasSizeGreaterThanOrEqualTo(2);
        assertThat(insertedFiles.getFirst().getDetectedType()).isEqualTo("zip");
        assertThat(insertedFiles.stream().anyMatch(file -> file.getParentZipFileId() != null)).isTrue();
        assertThat(insertedFiles.stream()
                .filter(file -> file.getParentZipFileId() != null)
                .anyMatch(file -> "success".equals(file.getPreviewStatus()))).isTrue();
        assertThat(insertedPreviews).isNotEmpty();
    }

    private byte[] createZipBytes() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            zipOutputStream.putNextEntry(new ZipEntry("slides.pdf"));
            zipOutputStream.write(createPdfBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.putNextEntry(new ZipEntry("__MACOSX/ignored.txt"));
            zipOutputStream.write("ignored".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }

    private byte[] createPdfBytes() throws Exception {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.addPage(new PDPage());
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
