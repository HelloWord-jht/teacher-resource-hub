package com.teacherresourcehub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.common.util.FileTypeDetector;
import com.teacherresourcehub.common.util.OfficeDocumentPdfConverter;
import com.teacherresourcehub.common.util.SafeZipExtractor;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ResourceFileServiceOfficePreviewTest {

    @TempDir
    Path tempDir;

    @Test
    void docxUploadShouldConvertToPdfAndGenerateTwoPreviewPages() throws Exception {
        ResourceMapper resourceMapper = Mockito.mock(ResourceMapper.class);
        ImportTaskMapper importTaskMapper = Mockito.mock(ImportTaskMapper.class);
        ResourceFileMapper resourceFileMapper = Mockito.mock(ResourceFileMapper.class);
        ResourceFilePreviewMapper previewMapper = Mockito.mock(ResourceFilePreviewMapper.class);
        ResourceFileProcessLogMapper logMapper = Mockito.mock(ResourceFileProcessLogMapper.class);

        ImportTask importTask = new ImportTask();
        importTask.setId(9L);
        importTask.setImportStatus("pending");
        when(importTaskMapper.selectById(9L)).thenReturn(importTask);

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
        when(resourceFileMapper.selectList(any(LambdaQueryWrapper.class))).thenAnswer(invocation -> new ArrayList<>(storedFiles));

        List<ResourceFilePreview> previews = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFilePreview preview = invocation.getArgument(0);
            previews.add(preview);
            return 1;
        }).when(previewMapper).insert(any(ResourceFilePreview.class));
        when(previewMapper.selectList(any())).thenAnswer(invocation -> new ArrayList<>(previews));

        List<ResourceFileProcessLog> logs = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFileProcessLog log = invocation.getArgument(0);
            logs.add(log);
            return 1;
        }).when(logMapper).insert(any(ResourceFileProcessLog.class));

        OfficeDocumentPdfConverter converter = (sourceFile, outputDirectory) -> {
            Files.createDirectories(outputDirectory);
            Path pdfPath = outputDirectory.resolve("converted-preview.pdf");
            createPdf(pdfPath, 2);
            return pdfPath;
        };

        ResourceFileService service = new ResourceFileServiceImpl(
                resourceMapper,
                importTaskMapper,
                resourceFileMapper,
                previewMapper,
                logMapper,
                new FileTypeDetector(),
                new SafeZipExtractor(),
                converter,
                tempDir,
                "/preview-files"
        );

        MockMultipartFile docxFile = new MockMultipartFile(
                "file",
                "家长会资料.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                createDocxLikeBytes()
        );

        service.uploadImportTaskFile(9L, docxFile);

        assertThat(storedFiles).hasSize(1);
        ResourceFile storedFile = storedFiles.getFirst();
        assertThat(storedFile.getDetectedType()).isEqualTo("docx");
        assertThat(storedFile.getPreviewStatus()).isEqualTo("success");
        assertThat(storedFile.getPreviewPageCount()).isEqualTo(2);
        assertThat(previews).hasSize(2);
        assertThat(previews).extracting(ResourceFilePreview::getPageNo).containsExactly(1, 2);
        assertThat(importTask.getImportStatus()).isEqualTo("success");
    }

    private void createPdf(Path filePath, int pageCount) throws java.io.IOException {
        try (PDDocument document = new PDDocument();
             OutputStream outputStream = Files.newOutputStream(filePath)) {
            for (int index = 0; index < pageCount; index++) {
                document.addPage(new PDPage());
            }
            document.save(outputStream);
        }
    }

    private byte[] createDocxLikeBytes() throws java.io.IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry("[Content_Types].xml"));
            zipOutputStream.write("<Types></Types>".getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.putNextEntry(new ZipEntry("word/document.xml"));
            zipOutputStream.write("<w:document></w:document>".getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }
}
