package com.teacherresourcehub.service;

import com.teacherresourcehub.common.enums.ResourceFileDetectedType;
import com.teacherresourcehub.common.enums.ResourceFilePreviewStatus;
import com.teacherresourcehub.entity.ResourceFile;
import com.teacherresourcehub.entity.ResourceFilePreview;
import com.teacherresourcehub.mapper.ResourceFileMapper;
import com.teacherresourcehub.mapper.ResourceFilePreviewMapper;
import com.teacherresourcehub.service.impl.ResourceFileQueryServiceImpl;
import com.teacherresourcehub.vo.WebResourcePreviewFileVO;
import com.teacherresourcehub.vo.WebResourcePreviewPageVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ResourceFileQueryServiceTest {

    @Test
    void shouldSelectPrimaryPreviewFileFirstWhenResourceHasPreviewableFiles() {
        ResourceFileMapper fileMapper = Mockito.mock(ResourceFileMapper.class);
        ResourceFilePreviewMapper previewMapper = Mockito.mock(ResourceFilePreviewMapper.class);

        when(fileMapper.selectList(any())).thenReturn(List.of(
                buildFile(11L, "备选课件.pdf", ResourceFileDetectedType.PDF, ResourceFilePreviewStatus.SUCCESS, 0, 0L),
                buildFile(12L, "主展示课件.png", ResourceFileDetectedType.IMAGE, ResourceFilePreviewStatus.SUCCESS, 1, 0L),
                buildFile(13L, "失败文档.docx", ResourceFileDetectedType.DOCX, ResourceFilePreviewStatus.FAILED, 0, 0L)
        ));
        when(previewMapper.selectList(any())).thenReturn(List.of(
                buildPreview(12L, 1, "/preview-files/12/page-1.png"),
                buildPreview(12L, 2, "/preview-files/12/page-2.png"),
                buildPreview(11L, 1, "/preview-files/11/page-1.png")
        ));

        ResourceFileQueryService service = new ResourceFileQueryServiceImpl(fileMapper, previewMapper);

        List<WebResourcePreviewFileVO> files = service.listWebPreviewFiles(99L);
        List<WebResourcePreviewPageVO> pages = service.listWebPreviewPages(99L, 12L);

        assertThat(files).hasSize(2);
        assertThat(files.getFirst().getId()).isEqualTo(12L);
        assertThat(files.getFirst().getCurrent()).isTrue();
        assertThat(files.getFirst().getPreviewPageCount()).isEqualTo(2);
        assertThat(files.get(1).getId()).isEqualTo(11L);
        assertThat(pages).extracting(WebResourcePreviewPageVO::getPageNo).containsExactly(1, 2);
    }

    private ResourceFile buildFile(Long id,
                                   String fileName,
                                   ResourceFileDetectedType detectedType,
                                   ResourceFilePreviewStatus previewStatus,
                                   Integer isPrimary,
                                   Long parentZipFileId) {
        ResourceFile file = new ResourceFile();
        file.setId(id);
        file.setResourceId(99L);
        file.setFileName(fileName);
        file.setOriginalFileName(fileName);
        file.setDetectedType(detectedType.name().toLowerCase());
        file.setPreviewStatus(previewStatus.getCode());
        file.setPreviewPageCount(2);
        file.setIsPrimary(isPrimary);
        file.setParentZipFileId(parentZipFileId);
        file.setSourceType("upload");
        file.setSortOrder(100);
        return file;
    }

    private ResourceFilePreview buildPreview(Long resourceFileId, Integer pageNo, String previewUrl) {
        ResourceFilePreview preview = new ResourceFilePreview();
        preview.setResourceFileId(resourceFileId);
        preview.setPageNo(pageNo);
        preview.setPreviewType("image");
        preview.setPreviewImageUrl(previewUrl);
        preview.setSortOrder(pageNo);
        return preview;
    }
}
