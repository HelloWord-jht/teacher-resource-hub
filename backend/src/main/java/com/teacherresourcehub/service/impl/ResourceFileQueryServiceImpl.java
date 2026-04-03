package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.common.enums.ResourceFileDetectedType;
import com.teacherresourcehub.common.enums.ResourceFilePreviewStatus;
import com.teacherresourcehub.entity.ResourceFile;
import com.teacherresourcehub.entity.ResourceFilePreview;
import com.teacherresourcehub.mapper.ResourceFileMapper;
import com.teacherresourcehub.mapper.ResourceFilePreviewMapper;
import com.teacherresourcehub.service.ResourceFileQueryService;
import com.teacherresourcehub.vo.WebResourcePreviewFileVO;
import com.teacherresourcehub.vo.WebResourcePreviewPageVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ResourceFileQueryServiceImpl implements ResourceFileQueryService {

    private final ResourceFileMapper resourceFileMapper;
    private final ResourceFilePreviewMapper resourceFilePreviewMapper;

    public ResourceFileQueryServiceImpl(ResourceFileMapper resourceFileMapper,
                                        ResourceFilePreviewMapper resourceFilePreviewMapper) {
        this.resourceFileMapper = resourceFileMapper;
        this.resourceFilePreviewMapper = resourceFilePreviewMapper;
    }

    @Override
    public List<WebResourcePreviewFileVO> listWebPreviewFiles(Long resourceId) {
        List<ResourceFile> files = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getResourceId, resourceId)
                .orderByDesc(ResourceFile::getSortOrder, ResourceFile::getId));
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        List<ResourceFile> previewableFiles = files.stream()
                .filter(file -> ResourceFilePreviewStatus.SUCCESS.getCode().equals(file.getPreviewStatus()))
                .filter(file -> file.getPreviewPageCount() != null && file.getPreviewPageCount() > 0)
                .sorted(previewComparator())
                .toList();
        if (previewableFiles.isEmpty()) {
            return Collections.emptyList();
        }

        boolean hasUserFacingFile = previewableFiles.stream()
                .anyMatch(file -> !Objects.equals("历史样张", file.getFileName()));
        if (hasUserFacingFile) {
            previewableFiles = previewableFiles.stream()
                    .filter(file -> !Objects.equals("历史样张", file.getFileName()))
                    .toList();
        }
        if (previewableFiles.isEmpty()) {
            return Collections.emptyList();
        }

        Long currentFileId = previewableFiles.getFirst().getId();
        return previewableFiles.stream()
                .map(file -> toWebFileVO(file, Objects.equals(file.getId(), currentFileId)))
                .toList();
    }

    @Override
    public List<WebResourcePreviewPageVO> listWebPreviewPages(Long resourceId, Long fileId) {
        List<ResourceFile> files = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getResourceId, resourceId)
                .eq(ResourceFile::getId, fileId)
                .last("LIMIT 1"));
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        if (!ResourceFilePreviewStatus.SUCCESS.getCode().equals(files.getFirst().getPreviewStatus())) {
            return Collections.emptyList();
        }

        return resourceFilePreviewMapper.selectList(new LambdaQueryWrapper<ResourceFilePreview>()
                        .eq(ResourceFilePreview::getResourceFileId, fileId)
                        .orderByAsc(ResourceFilePreview::getPageNo, ResourceFilePreview::getSortOrder, ResourceFilePreview::getId))
                .stream()
                .filter(preview -> Objects.equals(preview.getResourceFileId(), fileId))
                .map(this::toWebPageVO)
                .toList();
    }

    private WebResourcePreviewFileVO toWebFileVO(ResourceFile file, boolean current) {
        WebResourcePreviewFileVO vo = new WebResourcePreviewFileVO();
        vo.setId(file.getId());
        vo.setFileName(file.getFileName());
        vo.setFileType(file.getDetectedType());
        vo.setPreviewPageCount(file.getPreviewPageCount());
        vo.setPrimary(file.getIsPrimary() != null && file.getIsPrimary() == 1);
        vo.setCurrent(current);
        return vo;
    }

    private WebResourcePreviewPageVO toWebPageVO(ResourceFilePreview preview) {
        WebResourcePreviewPageVO vo = new WebResourcePreviewPageVO();
        vo.setPageNo(preview.getPageNo());
        vo.setPreviewType(preview.getPreviewType());
        vo.setPreviewImageUrl(preview.getPreviewImageUrl());
        vo.setPreviewTextExcerpt(preview.getPreviewTextExcerpt());
        return vo;
    }

    private Comparator<ResourceFile> previewComparator() {
        return Comparator
                .comparing((ResourceFile file) -> file.getIsPrimary() != null && file.getIsPrimary() == 1)
                .reversed()
                .thenComparing((ResourceFile file) -> previewTypeRank(ResourceFileDetectedType.fromCode(file.getDetectedType())))
                .thenComparing(ResourceFile::getSortOrder, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(ResourceFile::getId);
    }

    private int previewTypeRank(ResourceFileDetectedType detectedType) {
        return switch (detectedType) {
            case PPT, PPTX, PDF -> 0;
            case DOC, DOCX -> 1;
            case IMAGE -> 2;
            default -> 3;
        };
    }
}
