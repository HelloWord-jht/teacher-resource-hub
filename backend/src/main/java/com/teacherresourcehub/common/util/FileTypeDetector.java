package com.teacherresourcehub.common.util;

import com.teacherresourcehub.common.enums.ResourceFileDetectedType;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@Component
public class FileTypeDetector {

    private final Tika tika = new Tika();

    public DetectedFileInfo detect(String fileName, InputStream inputStream) throws IOException {
        String mimeType = tika.detect(inputStream, fileName);
        ResourceFileDetectedType detectedType = resolveDetectedType(fileName, mimeType);
        return new DetectedFileInfo(detectedType, mimeType);
    }

    private ResourceFileDetectedType resolveDetectedType(String fileName, String mimeType) {
        String lowerName = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
        String extension = StringUtils.getFilenameExtension(lowerName);

        if (mimeType != null) {
            if (mimeType.equalsIgnoreCase("application/pdf")) {
                return ResourceFileDetectedType.PDF;
            }
            if (mimeType.startsWith("image/")) {
                return ResourceFileDetectedType.IMAGE;
            }
            if (mimeType.contains("zip")) {
                if ("docx".equals(extension)) {
                    return ResourceFileDetectedType.DOCX;
                }
                if ("pptx".equals(extension)) {
                    return ResourceFileDetectedType.PPTX;
                }
                if ("xlsx".equals(extension)) {
                    return ResourceFileDetectedType.XLSX;
                }
                return ResourceFileDetectedType.ZIP;
            }
            if (mimeType.contains("msword")) {
                return ResourceFileDetectedType.DOC;
            }
            if (mimeType.contains("powerpoint")) {
                return "pptx".equals(extension) ? ResourceFileDetectedType.PPTX : ResourceFileDetectedType.PPT;
            }
            if (mimeType.contains("excel") || mimeType.contains("spreadsheet")) {
                return "xlsx".equals(extension) ? ResourceFileDetectedType.XLSX : ResourceFileDetectedType.XLS;
            }
            if (mimeType.equalsIgnoreCase("text/markdown")) {
                return ResourceFileDetectedType.MD;
            }
            if (mimeType.startsWith("text/")) {
                return "md".equals(extension) ? ResourceFileDetectedType.MD : ResourceFileDetectedType.TXT;
            }
        }

        if (extension == null || extension.isBlank()) {
            return ResourceFileDetectedType.UNKNOWN;
        }
        return switch (extension) {
            case "ppt" -> ResourceFileDetectedType.PPT;
            case "pptx" -> ResourceFileDetectedType.PPTX;
            case "doc" -> ResourceFileDetectedType.DOC;
            case "docx" -> ResourceFileDetectedType.DOCX;
            case "pdf" -> ResourceFileDetectedType.PDF;
            case "png", "jpg", "jpeg", "webp", "gif" -> ResourceFileDetectedType.IMAGE;
            case "zip" -> ResourceFileDetectedType.ZIP;
            case "txt" -> ResourceFileDetectedType.TXT;
            case "md" -> ResourceFileDetectedType.MD;
            case "xls" -> ResourceFileDetectedType.XLS;
            case "xlsx" -> ResourceFileDetectedType.XLSX;
            default -> ResourceFileDetectedType.UNKNOWN;
        };
    }

    public static class DetectedFileInfo {
        private final ResourceFileDetectedType detectedType;
        private final String mimeType;

        public DetectedFileInfo(ResourceFileDetectedType detectedType, String mimeType) {
            this.detectedType = detectedType;
            this.mimeType = mimeType;
        }

        public ResourceFileDetectedType getDetectedType() {
            return detectedType;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
}
