package com.teacherresourcehub.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SafeZipExtractorTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldExtractNormalEntriesAndSkipHiddenFiles() throws Exception {
        Path zipFile = tempDir.resolve("bundle.zip");
        Files.write(zipFile, createZip(
                "folder/lesson.pdf", "pdf",
                "__MACOSX/ignored.txt", "ignored",
                ".DS_Store", "ignored"
        ));

        SafeZipExtractor extractor = new SafeZipExtractor();
        List<SafeZipExtractor.ExtractedEntry> entries = extractor.extract(zipFile, tempDir.resolve("unzipped"));

        assertThat(entries).hasSize(1);
        assertThat(entries.getFirst().getArchiveEntryPath()).isEqualTo("folder/lesson.pdf");
        assertThat(Files.exists(entries.getFirst().getExtractedPath())).isTrue();
    }

    @Test
    void shouldRejectZipSlipEntry() throws Exception {
        Path zipFile = tempDir.resolve("slip.zip");
        Files.write(zipFile, createZip("../evil.txt", "bad"));

        SafeZipExtractor extractor = new SafeZipExtractor();

        assertThatThrownBy(() -> extractor.extract(zipFile, tempDir.resolve("unzipped")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("非法压缩包路径");
    }

    private byte[] createZip(String... entries) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            for (int i = 0; i < entries.length; i += 2) {
                zipOutputStream.putNextEntry(new ZipEntry(entries[i]));
                zipOutputStream.write(entries[i + 1].getBytes(StandardCharsets.UTF_8));
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }
}
