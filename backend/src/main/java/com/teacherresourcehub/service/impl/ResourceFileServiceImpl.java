package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.common.enums.ResourceFileDetectedType;
import com.teacherresourcehub.common.enums.ResourceFilePreviewStatus;
import com.teacherresourcehub.common.enums.ResourceFileSourceType;
import com.teacherresourcehub.common.enums.ResourcePreviewAvailableStatus;
import com.teacherresourcehub.common.enums.ResourcePreviewMode;
import com.teacherresourcehub.common.util.FileTypeDetector;
import com.teacherresourcehub.common.util.OfficeDocumentPdfConverter;
import com.teacherresourcehub.common.util.SafeZipExtractor;
import com.teacherresourcehub.entity.ImportTask;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourceFile;
import com.teacherresourcehub.entity.ResourceFilePreview;
import com.teacherresourcehub.entity.ResourceFileProcessLog;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.ImportTaskMapper;
import com.teacherresourcehub.mapper.ResourceFileMapper;
import com.teacherresourcehub.mapper.ResourceFilePreviewMapper;
import com.teacherresourcehub.mapper.ResourceFileProcessLogMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.service.ResourceFileService;
import com.teacherresourcehub.vo.AdminResourceFileDetailVO;
import com.teacherresourcehub.vo.AdminResourceFileItemVO;
import com.teacherresourcehub.vo.AdminResourceFileLogVO;
import com.teacherresourcehub.vo.AdminResourceFilePreviewVO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ResourceFileServiceImpl implements ResourceFileService {

    private final ResourceMapper resourceMapper;
    private final ImportTaskMapper importTaskMapper;
    private final ResourceFileMapper resourceFileMapper;
    private final ResourceFilePreviewMapper resourceFilePreviewMapper;
    private final ResourceFileProcessLogMapper resourceFileProcessLogMapper;
    private final FileTypeDetector fileTypeDetector;
    private final SafeZipExtractor safeZipExtractor;
    private final OfficeDocumentPdfConverter officeDocumentPdfConverter;
    private final Path storageRoot;
    private final String publicPreviewBaseUrl;

    @Autowired
    public ResourceFileServiceImpl(ResourceMapper resourceMapper,
                                   ImportTaskMapper importTaskMapper,
                                   ResourceFileMapper resourceFileMapper,
                                   ResourceFilePreviewMapper resourceFilePreviewMapper,
                                   ResourceFileProcessLogMapper resourceFileProcessLogMapper,
                                   FileTypeDetector fileTypeDetector,
                                   SafeZipExtractor safeZipExtractor,
                                   OfficeDocumentPdfConverter officeDocumentPdfConverter,
                                   @Value("${app.preview.storage-root:${user.home}/.teacher-resource-hub/preview-data}") String storageRoot,
                                   @Value("${app.preview.public-base-url:/preview-files}") String publicPreviewBaseUrl) {
        this(resourceMapper,
                importTaskMapper,
                resourceFileMapper,
                resourceFilePreviewMapper,
                resourceFileProcessLogMapper,
                fileTypeDetector,
                safeZipExtractor,
                officeDocumentPdfConverter,
                Path.of(storageRoot),
                publicPreviewBaseUrl);
    }

    public ResourceFileServiceImpl(ResourceMapper resourceMapper,
                                   ImportTaskMapper importTaskMapper,
                                   ResourceFileMapper resourceFileMapper,
                                   ResourceFilePreviewMapper resourceFilePreviewMapper,
                                   ResourceFileProcessLogMapper resourceFileProcessLogMapper,
                                   FileTypeDetector fileTypeDetector,
                                   SafeZipExtractor safeZipExtractor,
                                   OfficeDocumentPdfConverter officeDocumentPdfConverter,
                                   Path storageRoot,
                                   String publicPreviewBaseUrl) {
        this.resourceMapper = resourceMapper;
        this.importTaskMapper = importTaskMapper;
        this.resourceFileMapper = resourceFileMapper;
        this.resourceFilePreviewMapper = resourceFilePreviewMapper;
        this.resourceFileProcessLogMapper = resourceFileProcessLogMapper;
        this.fileTypeDetector = fileTypeDetector;
        this.safeZipExtractor = safeZipExtractor;
        this.officeDocumentPdfConverter = officeDocumentPdfConverter;
        this.storageRoot = storageRoot;
        this.publicPreviewBaseUrl = normalizePublicBaseUrl(publicPreviewBaseUrl);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadResourceFile(Long resourceId, MultipartFile file) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        ResourceFile resourceFile = storeAndCreateResourceFile(resourceId, null, null, file, ResourceFileSourceType.UPLOAD.getCode(), "");
        processFile(resourceFile);
        refreshResourcePreviewState(resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadImportTaskFile(Long importTaskId, MultipartFile file) {
        ImportTask importTask = importTaskMapper.selectById(importTaskId);
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        ResourceFile resourceFile = storeAndCreateResourceFile(null, importTaskId, null, file, ResourceFileSourceType.UPLOAD.getCode(), "");
        processFile(resourceFile);
        refreshImportTaskStats(importTaskId);
    }

    @Override
    public List<AdminResourceFileItemVO> listResourceFiles(Long resourceId) {
        return resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                        .eq(ResourceFile::getResourceId, resourceId)
                        .orderByDesc(ResourceFile::getIsPrimary, ResourceFile::getSortOrder, ResourceFile::getId))
                .stream()
                .map(this::toAdminItemVO)
                .toList();
    }

    @Override
    public List<AdminResourceFileItemVO> listImportTaskFiles(Long importTaskId) {
        return resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                        .eq(ResourceFile::getImportTaskId, importTaskId)
                        .orderByDesc(ResourceFile::getParentZipFileId, ResourceFile::getSortOrder, ResourceFile::getId))
                .stream()
                .map(this::toAdminItemVO)
                .toList();
    }

    @Override
    public AdminResourceFileDetailVO getResourceFileDetail(Long fileId) {
        ResourceFile resourceFile = getResourceFileOrThrow(fileId);
        AdminResourceFileDetailVO detail = new AdminResourceFileDetailVO();
        fillAdminItem(detail, resourceFile);
        detail.setStoragePath(resourceFile.getStoragePath());
        detail.setPreviews(listFilePreviews(fileId));
        return detail;
    }

    @Override
    public List<AdminResourceFilePreviewVO> listFilePreviews(Long fileId) {
        return resourceFilePreviewMapper.selectList(new LambdaQueryWrapper<ResourceFilePreview>()
                        .eq(ResourceFilePreview::getResourceFileId, fileId)
                        .orderByAsc(ResourceFilePreview::getPageNo, ResourceFilePreview::getSortOrder, ResourceFilePreview::getId))
                .stream()
                .map(this::toAdminPreviewVO)
                .toList();
    }

    @Override
    public List<AdminResourceFileLogVO> listFileLogs(Long fileId) {
        return resourceFileProcessLogMapper.selectList(new LambdaQueryWrapper<ResourceFileProcessLog>()
                        .eq(ResourceFileProcessLog::getResourceFileId, fileId)
                        .orderByDesc(ResourceFileProcessLog::getCreatedAt, ResourceFileProcessLog::getId))
                .stream()
                .map(this::toAdminLogVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void regeneratePreview(Long fileId) {
        ResourceFile resourceFile = getResourceFileOrThrow(fileId);
        resourceFilePreviewMapper.delete(new LambdaQueryWrapper<ResourceFilePreview>()
                .eq(ResourceFilePreview::getResourceFileId, fileId));
        processFile(resourceFile);
        refreshResourcePreviewState(resourceFile.getResourceId());
        refreshImportTaskStats(resourceFile.getImportTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPrimaryFile(Long resourceId, Long fileId) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        ResourceFile selectedFile = getResourceFileOrThrow(fileId);
        if (!Objects.equals(selectedFile.getResourceId(), resourceId)) {
            throw new BusinessException("目标文件不属于当前资源");
        }
        List<ResourceFile> files = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getResourceId, resourceId));
        for (ResourceFile file : files) {
            int targetPrimary = Objects.equals(file.getId(), fileId) ? 1 : 0;
            if (file.getIsPrimary() == null || file.getIsPrimary() != targetPrimary) {
                file.setIsPrimary(targetPrimary);
                resourceFileMapper.updateById(file);
            }
        }
        resource.setPrimaryFileId(fileId);
        resourceMapper.updateById(resource);
        refreshResourcePreviewState(resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) {
        ResourceFile resourceFile = getResourceFileOrThrow(fileId);
        resourceFilePreviewMapper.delete(new LambdaQueryWrapper<ResourceFilePreview>()
                .eq(ResourceFilePreview::getResourceFileId, fileId));
        resourceFileMapper.deleteById(fileId);
        refreshResourcePreviewState(resourceFile.getResourceId());
        refreshImportTaskStats(resourceFile.getImportTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeImportTaskPreview(Long importTaskId) {
        ImportTask importTask = importTaskMapper.selectById(importTaskId);
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        List<ResourceFile> files = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getImportTaskId, importTaskId)
                .orderByAsc(ResourceFile::getId));
        for (ResourceFile file : files) {
            resourceFilePreviewMapper.delete(new LambdaQueryWrapper<ResourceFilePreview>()
                    .eq(ResourceFilePreview::getResourceFileId, file.getId()));
            processFile(file);
        }
        refreshImportTaskStats(importTaskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindImportTaskFilesToResource(Long importTaskId, Long resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        List<ResourceFile> files = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getImportTaskId, importTaskId)
                .orderByDesc(ResourceFile::getSortOrder, ResourceFile::getId));
        if (files == null || files.isEmpty()) {
            return;
        }

        boolean hasPrimary = files.stream().anyMatch(file -> file.getIsPrimary() != null && file.getIsPrimary() == 1);
        ResourceFile primaryCandidate = files.stream()
                .filter(file -> ResourceFilePreviewStatus.SUCCESS.getCode().equals(file.getPreviewStatus()))
                .filter(file -> file.getPreviewPageCount() != null && file.getPreviewPageCount() > 0)
                .sorted(primaryComparator())
                .findFirst()
                .orElse(null);

        for (ResourceFile file : files) {
            file.setResourceId(resourceId);
            if (!hasPrimary && primaryCandidate != null && Objects.equals(file.getId(), primaryCandidate.getId())) {
                file.setIsPrimary(1);
            }
            resourceFileMapper.updateById(file);
        }
        refreshResourcePreviewState(resourceId);
    }

    private ResourceFile storeAndCreateResourceFile(Long resourceId,
                                                    Long importTaskId,
                                                    Long parentZipFileId,
                                                    MultipartFile multipartFile,
                                                    String sourceType,
                                                    String archiveEntryPath) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalFileName = StringUtils.hasText(multipartFile.getOriginalFilename())
                ? multipartFile.getOriginalFilename()
                : "unnamed";
        String extension = StringUtils.getFilenameExtension(originalFileName);
        Path fileDirectory = buildOriginalFileDirectory(resourceId, importTaskId);
        try {
            Files.createDirectories(fileDirectory);
            Path storedFilePath = fileDirectory.resolve(buildStoredFileName(extension));
            Files.copy(multipartFile.getInputStream(), storedFilePath, StandardCopyOption.REPLACE_EXISTING);

            FileTypeDetector.DetectedFileInfo detectedFileInfo;
            try (InputStream inputStream = Files.newInputStream(storedFilePath)) {
                detectedFileInfo = fileTypeDetector.detect(originalFileName, inputStream);
            }

            ResourceFile resourceFile = new ResourceFile();
            resourceFile.setResourceId(resourceId);
            resourceFile.setImportTaskId(importTaskId);
            resourceFile.setParentZipFileId(parentZipFileId);
            resourceFile.setFileName(originalFileName);
            resourceFile.setOriginalFileName(originalFileName);
            resourceFile.setFileExt(extension == null ? "" : extension.toLowerCase(Locale.ROOT));
            resourceFile.setDetectedType(detectedFileInfo.getDetectedType().getCode());
            resourceFile.setMimeType(detectedFileInfo.getMimeType());
            resourceFile.setFileSize(multipartFile.getSize());
            resourceFile.setStoragePath(storedFilePath.toString());
            resourceFile.setArchiveEntryPath(archiveEntryPath);
            resourceFile.setSourceType(sourceType);
            resourceFile.setSortOrder(100);
            resourceFile.setIsPrimary(0);
            resourceFile.setPreviewStatus(ResourceFilePreviewStatus.PENDING.getCode());
            resourceFile.setPreviewPageCount(0);
            resourceFile.setPreviewErrorMessage("");
            resourceFileMapper.insert(resourceFile);
            writeLog(resourceFile.getId(), "detect_type", "success",
                    "识别文件成功，类型=%s，MIME=%s".formatted(resourceFile.getDetectedType(), resourceFile.getMimeType()));
            return resourceFile;
        } catch (IOException exception) {
            throw new BusinessException("保存上传文件失败：" + exception.getMessage());
        }
    }

    private void processFile(ResourceFile resourceFile) {
        updatePreviewStatus(resourceFile, ResourceFilePreviewStatus.PROCESSING, 0, "");
        ResourceFileDetectedType detectedType = ResourceFileDetectedType.fromCode(resourceFile.getDetectedType());

        try {
            switch (detectedType) {
                case IMAGE -> generateImagePreview(resourceFile);
                case PDF -> generatePdfPreview(resourceFile);
                case PPT, PPTX, DOC, DOCX -> generateOfficeDocumentPreview(resourceFile);
                case TXT, MD -> generateTextPreview(resourceFile);
                case ZIP -> expandZipAndProcessChildren(resourceFile);
                default -> markUnsupported(resourceFile, "当前文件格式暂不支持在线预览");
            }
        } catch (Exception exception) {
            updatePreviewStatus(resourceFile, ResourceFilePreviewStatus.FAILED, 0, exception.getMessage());
            writeLog(resourceFile.getId(), "render_preview", "failed", exception.getMessage());
        }
    }

    private void generateImagePreview(ResourceFile resourceFile) throws IOException {
        Path sourcePath = Path.of(resourceFile.getStoragePath());
        Path previewDirectory = buildPreviewDirectory(resourceFile);
        Files.createDirectories(previewDirectory);

        String extension = safeImageExtension(resourceFile.getFileExt());
        Path targetFile = previewDirectory.resolve("page-1." + extension);
        Files.copy(sourcePath, targetFile, StandardCopyOption.REPLACE_EXISTING);

        BufferedImage bufferedImage = ImageIO.read(sourcePath.toFile());
        ResourceFilePreview preview = new ResourceFilePreview();
        preview.setResourceFileId(resourceFile.getId());
        preview.setPageNo(1);
        preview.setPreviewType("image");
        preview.setPreviewImageUrl(buildPreviewUrl(targetFile));
        preview.setWidth(bufferedImage == null ? null : bufferedImage.getWidth());
        preview.setHeight(bufferedImage == null ? null : bufferedImage.getHeight());
        preview.setSortOrder(1);
        resourceFilePreviewMapper.insert(preview);

        updatePreviewStatus(resourceFile, ResourceFilePreviewStatus.SUCCESS, 1, "");
        writeLog(resourceFile.getId(), "save_preview", "success", "图片样张已生成");
    }

    private void generatePdfPreview(ResourceFile resourceFile) throws IOException {
        generatePdfPreview(resourceFile, Path.of(resourceFile.getStoragePath()), true);
    }

    private void generateOfficeDocumentPreview(ResourceFile resourceFile) throws IOException, InterruptedException {
        Path conversionDirectory = buildConversionDirectory(resourceFile);
        Files.createDirectories(conversionDirectory);
        writeLog(resourceFile.getId(), "convert_pdf", "processing", "开始将 Office 文件转换为 PDF");
        Path convertedPdf = officeDocumentPdfConverter.convertToPdf(Path.of(resourceFile.getStoragePath()), conversionDirectory);
        writeLog(resourceFile.getId(), "convert_pdf", "success", "Office 文件已转换为 PDF");
        generatePdfPreview(resourceFile, convertedPdf, false);
    }

    private void generatePdfPreview(ResourceFile resourceFile, Path sourcePath, boolean directPdf) throws IOException {
        Path previewDirectory = buildPreviewDirectory(resourceFile);
        Files.createDirectories(previewDirectory);

        try (PDDocument document = Loader.loadPDF(sourcePath.toFile())) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = Math.min(document.getNumberOfPages(), 2);
            for (int index = 0; index < pageCount; index++) {
                BufferedImage renderedImage = renderer.renderImageWithDPI(index, 144);
                Path targetFile = previewDirectory.resolve("page-" + (index + 1) + ".png");
                ImageIO.write(renderedImage, "png", targetFile.toFile());

                ResourceFilePreview preview = new ResourceFilePreview();
                preview.setResourceFileId(resourceFile.getId());
                preview.setPageNo(index + 1);
                preview.setPreviewType("image");
                preview.setPreviewImageUrl(buildPreviewUrl(targetFile));
                preview.setWidth(renderedImage.getWidth());
                preview.setHeight(renderedImage.getHeight());
                preview.setSortOrder(index + 1);
                resourceFilePreviewMapper.insert(preview);
            }

            updatePreviewStatus(resourceFile, ResourceFilePreviewStatus.SUCCESS, pageCount, "");
            writeLog(resourceFile.getId(), "render_preview", "success", directPdf ? "PDF样张渲染成功" : "Office 文件样张渲染成功");
        }
    }

    private void generateTextPreview(ResourceFile resourceFile) throws IOException {
        String content = Files.readString(Path.of(resourceFile.getStoragePath()), StandardCharsets.UTF_8);
        String excerpt = content.length() > 240 ? content.substring(0, 240) : content;

        ResourceFilePreview preview = new ResourceFilePreview();
        preview.setResourceFileId(resourceFile.getId());
        preview.setPageNo(1);
        preview.setPreviewType("text");
        preview.setPreviewTextExcerpt(excerpt);
        preview.setSortOrder(1);
        resourceFilePreviewMapper.insert(preview);

        updatePreviewStatus(resourceFile, ResourceFilePreviewStatus.SUCCESS, 1, "");
        writeLog(resourceFile.getId(), "save_preview", "success", "文本样张已生成");
    }

    private void expandZipAndProcessChildren(ResourceFile zipFile) throws IOException {
        writeLog(zipFile.getId(), "unzip", "processing", "开始解压ZIP文件");
        Path extractDirectory = buildExtractDirectory(zipFile);
        cleanupZipChildren(zipFile, extractDirectory);
        Files.createDirectories(extractDirectory);
        List<SafeZipExtractor.ExtractedEntry> extractedEntries = safeZipExtractor.extract(Path.of(zipFile.getStoragePath()), extractDirectory);
        if (extractedEntries.isEmpty()) {
            markUnsupported(zipFile, "ZIP 内没有可处理文件");
            return;
        }

        int successCount = 0;
        for (SafeZipExtractor.ExtractedEntry extractedEntry : extractedEntries) {
            ResourceFile childFile = createExtractedChildFile(zipFile, extractedEntry);
            processFile(childFile);
            if (ResourceFilePreviewStatus.SUCCESS.getCode().equals(childFile.getPreviewStatus())
                    && childFile.getPreviewPageCount() != null
                    && childFile.getPreviewPageCount() > 0) {
                successCount++;
            }
        }

        if (successCount > 0) {
            updatePreviewStatus(zipFile, ResourceFilePreviewStatus.SUCCESS, 0, "");
            writeLog(zipFile.getId(), "unzip", "success", "ZIP 文件解压完成，成功生成 %s 个子文件样张".formatted(successCount));
        } else {
            updatePreviewStatus(zipFile, ResourceFilePreviewStatus.FAILED, 0, "ZIP 内文件均未生成样张");
            writeLog(zipFile.getId(), "unzip", "failed", "ZIP 内文件均未生成样张");
        }
    }

    private void cleanupZipChildren(ResourceFile zipFile, Path extractDirectory) throws IOException {
        List<ResourceFile> childFiles = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getParentZipFileId, zipFile.getId())
                .orderByAsc(ResourceFile::getId));
        for (ResourceFile childFile : childFiles) {
            resourceFilePreviewMapper.delete(new LambdaQueryWrapper<ResourceFilePreview>()
                    .eq(ResourceFilePreview::getResourceFileId, childFile.getId()));
            resourceFileProcessLogMapper.delete(new LambdaQueryWrapper<ResourceFileProcessLog>()
                    .eq(ResourceFileProcessLog::getResourceFileId, childFile.getId()));
            resourceFileMapper.deleteById(childFile.getId());
        }
        if (Files.exists(extractDirectory)) {
            try (Stream<Path> paths = Files.walk(extractDirectory)) {
                paths.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                            }
                        });
            }
        }
    }

    private ResourceFile createExtractedChildFile(ResourceFile zipFile,
                                                  SafeZipExtractor.ExtractedEntry extractedEntry) throws IOException {
        FileTypeDetector.DetectedFileInfo detectedFileInfo;
        try (InputStream inputStream = Files.newInputStream(extractedEntry.getExtractedPath())) {
            detectedFileInfo = fileTypeDetector.detect(extractedEntry.getExtractedPath().getFileName().toString(), inputStream);
        }

        ResourceFile childFile = new ResourceFile();
        childFile.setResourceId(zipFile.getResourceId());
        childFile.setImportTaskId(zipFile.getImportTaskId());
        childFile.setParentZipFileId(zipFile.getId());
        childFile.setFileName(extractedEntry.getExtractedPath().getFileName().toString());
        childFile.setOriginalFileName(extractedEntry.getExtractedPath().getFileName().toString());
        childFile.setFileExt(getExtension(childFile.getFileName()));
        childFile.setDetectedType(detectedFileInfo.getDetectedType().getCode());
        childFile.setMimeType(detectedFileInfo.getMimeType());
        childFile.setFileSize(extractedEntry.getFileSize());
        childFile.setStoragePath(extractedEntry.getExtractedPath().toString());
        childFile.setArchiveEntryPath(extractedEntry.getArchiveEntryPath());
        childFile.setSourceType(ResourceFileSourceType.EXTRACTED.getCode());
        childFile.setSortOrder(90);
        childFile.setIsPrimary(0);
        childFile.setPreviewStatus(ResourceFilePreviewStatus.PENDING.getCode());
        childFile.setPreviewPageCount(0);
        childFile.setPreviewErrorMessage("");
        resourceFileMapper.insert(childFile);
        writeLog(childFile.getId(), "detect_type", "success",
                "识别ZIP子文件成功，类型=%s，路径=%s".formatted(childFile.getDetectedType(), childFile.getArchiveEntryPath()));
        return childFile;
    }

    private void markUnsupported(ResourceFile resourceFile, String message) {
        updatePreviewStatus(resourceFile, ResourceFilePreviewStatus.UNSUPPORTED, 0, message);
        writeLog(resourceFile.getId(), "render_preview", "skipped", message);
    }

    private void updatePreviewStatus(ResourceFile resourceFile,
                                     ResourceFilePreviewStatus previewStatus,
                                     int pageCount,
                                     String errorMessage) {
        resourceFile.setPreviewStatus(previewStatus.getCode());
        resourceFile.setPreviewPageCount(pageCount);
        resourceFile.setPreviewErrorMessage(errorMessage == null ? "" : errorMessage);
        resourceFileMapper.updateById(resourceFile);
    }

    private void refreshResourcePreviewState(Long resourceId) {
        if (resourceId == null) {
            return;
        }
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return;
        }
        List<ResourceFile> files = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getResourceId, resourceId)
                .orderByDesc(ResourceFile::getSortOrder, ResourceFile::getId));
        if (files == null || files.isEmpty()) {
            resource.setPrimaryFileId(null);
            resource.setPreviewAvailableStatus(ResourcePreviewAvailableStatus.NONE.getCode());
            resource.setPreviewMode(ResourcePreviewMode.SINGLE.getCode());
            resource.setPreviewCount(0);
            resourceMapper.updateById(resource);
            return;
        }

        List<ResourceFile> successFiles = files.stream()
                .filter(file -> ResourceFilePreviewStatus.SUCCESS.getCode().equals(file.getPreviewStatus()))
                .filter(file -> file.getPreviewPageCount() != null && file.getPreviewPageCount() > 0)
                .sorted(primaryComparator())
                .toList();

        if (!successFiles.isEmpty()) {
            ResourceFile primaryFile = successFiles.getFirst();
            resource.setPrimaryFileId(primaryFile.getId());
            resource.setPreviewAvailableStatus(successFiles.size() == files.stream()
                    .filter(file -> !ResourceFileDetectedType.ZIP.getCode().equals(file.getDetectedType()))
                    .count() ? ResourcePreviewAvailableStatus.AVAILABLE.getCode() : ResourcePreviewAvailableStatus.PARTIAL.getCode());
            resource.setPreviewMode(resolvePreviewMode(files, successFiles));
            resource.setPreviewCount(successFiles.stream()
                    .map(ResourceFile::getPreviewPageCount)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum());
            if (primaryFile.getIsPrimary() == null || primaryFile.getIsPrimary() == 0) {
                primaryFile.setIsPrimary(1);
                resourceFileMapper.updateById(primaryFile);
            }
        } else {
            resource.setPrimaryFileId(null);
            resource.setPreviewAvailableStatus(ResourcePreviewAvailableStatus.NONE.getCode());
            resource.setPreviewMode(files.stream().anyMatch(file -> file.getParentZipFileId() != null)
                    ? ResourcePreviewMode.ZIP_BUNDLE.getCode()
                    : ResourcePreviewMode.SINGLE.getCode());
            resource.setPreviewCount(0);
        }
        resourceMapper.updateById(resource);
    }

    private void refreshImportTaskStats(Long importTaskId) {
        if (importTaskId == null) {
            return;
        }
        ImportTask importTask = importTaskMapper.selectById(importTaskId);
        if (importTask == null) {
            return;
        }
        List<ResourceFile> files = resourceFileMapper.selectList(new LambdaQueryWrapper<ResourceFile>()
                .eq(ResourceFile::getImportTaskId, importTaskId)
                .orderByAsc(ResourceFile::getId));
        List<ResourceFile> previewRelevantFiles = files.stream()
                .filter(file -> !ResourceFileDetectedType.ZIP.getCode().equals(file.getDetectedType()))
                .toList();
        List<ResourceFile> zipFiles = files.stream()
                .filter(file -> ResourceFileDetectedType.ZIP.getCode().equals(file.getDetectedType()))
                .toList();
        int total = files.size();
        int recognized = (int) files.stream().filter(file -> !ResourceFileDetectedType.UNKNOWN.getCode().equals(file.getDetectedType())).count();
        int processed = (int) files.stream().filter(file -> !ResourceFilePreviewStatus.PENDING.getCode().equals(file.getPreviewStatus())).count();
        int success = (int) previewRelevantFiles.stream().filter(file -> ResourceFilePreviewStatus.SUCCESS.getCode().equals(file.getPreviewStatus()) && file.getPreviewPageCount() != null && file.getPreviewPageCount() > 0).count();
        int failed = (int) previewRelevantFiles.stream().filter(file -> ResourceFilePreviewStatus.FAILED.getCode().equals(file.getPreviewStatus())).count();
        int unsupported = (int) previewRelevantFiles.stream().filter(file -> ResourceFilePreviewStatus.UNSUPPORTED.getCode().equals(file.getPreviewStatus())).count();
        boolean hasZip = !zipFiles.isEmpty();
        boolean zipFailed = zipFiles.stream().anyMatch(file -> ResourceFilePreviewStatus.FAILED.getCode().equals(file.getPreviewStatus()));
        boolean zipProcessing = zipFiles.stream().anyMatch(file -> ResourceFilePreviewStatus.PENDING.getCode().equals(file.getPreviewStatus())
                || ResourceFilePreviewStatus.PROCESSING.getCode().equals(file.getPreviewStatus()));

        importTask.setTotalFileCount(total);
        importTask.setRecognizedFileCount(recognized);
        importTask.setProcessedFileCount(processed);
        importTask.setPreviewSuccessCount(success);
        importTask.setPreviewFailedCount(failed);
        importTask.setUnsupportedFileCount(unsupported);
        if (!hasZip) {
            importTask.setUnzipStatus("not_applicable");
        } else if (zipProcessing) {
            importTask.setUnzipStatus("processing");
        } else if (zipFailed) {
            importTask.setUnzipStatus("failed");
        } else {
            importTask.setUnzipStatus("success");
        }
        importTask.setPreviewStatusSummary(
                "{\"pending\":%d,\"processing\":0,\"success\":%d,\"failed\":%d,\"unsupported\":%d}"
                        .formatted(Math.max(total - processed, 0), success, failed, unsupported)
        );
        if (total == 0) {
            importTask.setImportStatus("pending");
        } else if (!previewRelevantFiles.isEmpty()
                && success == previewRelevantFiles.size()
                && failed == 0
                && unsupported == 0
                && !"failed".equals(importTask.getUnzipStatus())) {
            importTask.setImportStatus("success");
        } else if (success > 0 && (failed > 0 || unsupported > 0 || success < previewRelevantFiles.size() || "failed".equals(importTask.getUnzipStatus()))) {
            importTask.setImportStatus("partial_success");
        } else if (processed > 0) {
            importTask.setImportStatus("failed");
        } else {
            importTask.setImportStatus("processing");
        }
        importTaskMapper.updateById(importTask);
    }

    private String resolvePreviewMode(List<ResourceFile> allFiles, List<ResourceFile> successFiles) {
        if (allFiles.stream().anyMatch(file -> file.getParentZipFileId() != null)) {
            return ResourcePreviewMode.ZIP_BUNDLE.getCode();
        }
        return successFiles.size() > 1 ? ResourcePreviewMode.MULTI.getCode() : ResourcePreviewMode.SINGLE.getCode();
    }

    private Comparator<ResourceFile> primaryComparator() {
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

    private void writeLog(Long resourceFileId, String stepName, String stepStatus, String message) {
        ResourceFileProcessLog log = new ResourceFileProcessLog();
        log.setResourceFileId(resourceFileId);
        log.setStepName(stepName);
        log.setStepStatus(stepStatus);
        log.setMessage(message);
        log.setCreatedAt(LocalDateTime.now());
        resourceFileProcessLogMapper.insert(log);
    }

    private Path buildOriginalFileDirectory(Long resourceId, Long importTaskId) {
        if (resourceId != null) {
            return storageRoot.resolve("resource-" + resourceId).resolve("source");
        }
        return storageRoot.resolve("import-task-" + importTaskId).resolve("source");
    }

    private Path buildPreviewDirectory(ResourceFile resourceFile) {
        if (resourceFile.getResourceId() != null) {
            return storageRoot.resolve("resource-" + resourceFile.getResourceId())
                    .resolve("previews")
                    .resolve("file-" + resourceFile.getId());
        }
        return storageRoot.resolve("import-task-" + resourceFile.getImportTaskId())
                .resolve("previews")
                .resolve("file-" + resourceFile.getId());
    }

    private Path buildConversionDirectory(ResourceFile resourceFile) {
        if (resourceFile.getResourceId() != null) {
            return storageRoot.resolve("resource-" + resourceFile.getResourceId())
                    .resolve("converted")
                    .resolve("file-" + resourceFile.getId());
        }
        return storageRoot.resolve("import-task-" + resourceFile.getImportTaskId())
                .resolve("converted")
                .resolve("file-" + resourceFile.getId());
    }

    private Path buildExtractDirectory(ResourceFile resourceFile) {
        if (resourceFile.getResourceId() != null) {
            return storageRoot.resolve("resource-" + resourceFile.getResourceId())
                    .resolve("extracted")
                    .resolve("zip-" + resourceFile.getId());
        }
        return storageRoot.resolve("import-task-" + resourceFile.getImportTaskId())
                .resolve("extracted")
                .resolve("zip-" + resourceFile.getId());
    }

    private String buildStoredFileName(String extension) {
        String suffix = StringUtils.hasText(extension) ? "." + extension.toLowerCase(Locale.ROOT) : "";
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().replace("-", "") + suffix;
    }

    private String buildPreviewUrl(Path previewFilePath) {
        String relativePath = storageRoot.relativize(previewFilePath).toString().replace("\\", "/");
        return publicPreviewBaseUrl + "/" + relativePath;
    }

    private String normalizePublicBaseUrl(String publicPreviewBaseUrl) {
        if (publicPreviewBaseUrl == null || publicPreviewBaseUrl.isBlank()) {
            return "/preview-files";
        }
        String normalized = publicPreviewBaseUrl.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String safeImageExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return "png";
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "png", "jpg", "jpeg", "webp", "gif" -> normalized;
            default -> "png";
        };
    }

    private String getExtension(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension == null ? "" : extension.toLowerCase(Locale.ROOT);
    }

    private ResourceFile getResourceFileOrThrow(Long fileId) {
        ResourceFile resourceFile = resourceFileMapper.selectById(fileId);
        if (resourceFile == null) {
            throw new BusinessException("资源文件不存在");
        }
        return resourceFile;
    }

    private AdminResourceFileItemVO toAdminItemVO(ResourceFile resourceFile) {
        AdminResourceFileItemVO vo = new AdminResourceFileItemVO();
        fillAdminItem(vo, resourceFile);
        return vo;
    }

    private void fillAdminItem(AdminResourceFileItemVO target, ResourceFile resourceFile) {
        target.setId(resourceFile.getId());
        target.setResourceId(resourceFile.getResourceId());
        target.setImportTaskId(resourceFile.getImportTaskId());
        target.setParentZipFileId(resourceFile.getParentZipFileId());
        target.setFileName(resourceFile.getFileName());
        target.setFileType(resourceFile.getDetectedType());
        target.setMimeType(resourceFile.getMimeType());
        target.setFileSize(resourceFile.getFileSize());
        target.setSourceType(resourceFile.getSourceType());
        target.setArchiveEntryPath(resourceFile.getArchiveEntryPath());
        target.setIsPrimary(resourceFile.getIsPrimary());
        target.setPreviewStatus(resourceFile.getPreviewStatus());
        target.setPreviewPageCount(resourceFile.getPreviewPageCount());
        target.setPreviewErrorMessage(resourceFile.getPreviewErrorMessage());
    }

    private AdminResourceFilePreviewVO toAdminPreviewVO(ResourceFilePreview preview) {
        AdminResourceFilePreviewVO vo = new AdminResourceFilePreviewVO();
        vo.setPageNo(preview.getPageNo());
        vo.setPreviewType(preview.getPreviewType());
        vo.setPreviewImageUrl(preview.getPreviewImageUrl());
        vo.setPreviewTextExcerpt(preview.getPreviewTextExcerpt());
        vo.setWidth(preview.getWidth());
        vo.setHeight(preview.getHeight());
        return vo;
    }

    private AdminResourceFileLogVO toAdminLogVO(ResourceFileProcessLog log) {
        AdminResourceFileLogVO vo = new AdminResourceFileLogVO();
        vo.setId(log.getId());
        vo.setStepName(log.getStepName());
        vo.setStepStatus(log.getStepStatus());
        vo.setMessage(log.getMessage());
        vo.setCreatedAt(log.getCreatedAt());
        return vo;
    }
}
