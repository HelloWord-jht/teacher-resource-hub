package com.teacherresourcehub.service;

import com.teacherresourcehub.entity.ImportTask;
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
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ResourceFileServiceUploadTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldUploadImageAndGenerateSinglePreviewImmediately() throws Exception {
        ResourceMapper resourceMapper = Mockito.mock(ResourceMapper.class);
        ImportTaskMapper importTaskMapper = Mockito.mock(ImportTaskMapper.class);
        ResourceFileMapper resourceFileMapper = Mockito.mock(ResourceFileMapper.class);
        ResourceFilePreviewMapper previewMapper = Mockito.mock(ResourceFilePreviewMapper.class);
        ResourceFileProcessLogMapper logMapper = Mockito.mock(ResourceFileProcessLogMapper.class);

        Resource resource = new Resource();
        resource.setId(1L);
        when(resourceMapper.selectById(1L)).thenReturn(resource);

        List<ResourceFile> insertedFiles = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFile file = invocation.getArgument(0);
            file.setId((long) (insertedFiles.size() + 1));
            insertedFiles.add(file);
            return 1;
        }).when(resourceFileMapper).insert(any(ResourceFile.class));

        List<ResourceFilePreview> insertedPreviews = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFilePreview preview = invocation.getArgument(0);
            insertedPreviews.add(preview);
            return 1;
        }).when(previewMapper).insert(any(ResourceFilePreview.class));

        List<ResourceFileProcessLog> insertedLogs = new ArrayList<>();
        doAnswer(invocation -> {
            ResourceFileProcessLog log = invocation.getArgument(0);
            insertedLogs.add(log);
            return 1;
        }).when(logMapper).insert(any(ResourceFileProcessLog.class));

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
                "sample.png",
                "image/png",
                createPngBytes()
        );

        service.uploadResourceFile(1L, multipartFile);

        assertThat(insertedFiles).hasSize(1);
        assertThat(insertedFiles.getFirst().getPreviewStatus()).isEqualTo("success");
        assertThat(insertedFiles.getFirst().getPreviewPageCount()).isEqualTo(1);
        assertThat(insertedPreviews).hasSize(1);
        assertThat(insertedPreviews.getFirst().getPreviewImageUrl()).contains("/preview-files/");
        assertThat(insertedLogs).extracting(ResourceFileProcessLog::getStepName)
                .contains("detect_type", "save_preview");
    }

    private byte[] createPngBytes() throws Exception {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        }
    }
}
