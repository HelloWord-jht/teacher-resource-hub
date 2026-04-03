package com.teacherresourcehub.common.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class SafeZipExtractor {

    public List<ExtractedEntry> extract(Path zipFilePath, Path destinationRoot) throws IOException {
        Files.createDirectories(destinationRoot);
        Path normalizedRoot = destinationRoot.toAbsolutePath().normalize();
        List<ExtractedEntry> extractedEntries = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(zipFilePath);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String entryName = sanitizeEntryName(entry.getName());
                if (shouldIgnoreEntry(entryName)) {
                    continue;
                }

                Path targetPath = normalizedRoot.resolve(entryName).normalize();
                if (!targetPath.startsWith(normalizedRoot)) {
                    throw new IllegalArgumentException("非法压缩包路径");
                }

                Files.createDirectories(targetPath.getParent());
                Files.copy(zipInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                extractedEntries.add(new ExtractedEntry(entryName, targetPath, Files.size(targetPath)));
            }
        }
        return extractedEntries;
    }

    private String sanitizeEntryName(String entryName) {
        return entryName == null ? "" : entryName.replace("\\", "/");
    }

    private boolean shouldIgnoreEntry(String entryName) {
        if (entryName.isBlank()) {
            return true;
        }
        String normalized = entryName.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("__macosx/") || normalized.endsWith("/.ds_store") || normalized.equals(".ds_store")) {
            return true;
        }
        String[] segments = normalized.split("/");
        for (String segment : segments) {
            if (segment.startsWith(".") && !segment.equals(".") && !segment.equals("..")) {
                return true;
            }
            if (segment.startsWith("~$")) {
                return true;
            }
        }
        return false;
    }

    public static class ExtractedEntry {
        private final String archiveEntryPath;
        private final Path extractedPath;
        private final long fileSize;

        public ExtractedEntry(String archiveEntryPath, Path extractedPath, long fileSize) {
            this.archiveEntryPath = archiveEntryPath;
            this.extractedPath = extractedPath;
            this.fileSize = fileSize;
        }

        public String getArchiveEntryPath() {
            return archiveEntryPath;
        }

        public Path getExtractedPath() {
            return extractedPath;
        }

        public long getFileSize() {
            return fileSize;
        }
    }
}
