package com.teacherresourcehub.common.util;

import com.teacherresourcehub.common.enums.ResourceFileDetectedType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class FileTypeDetectorTest {

    private final FileTypeDetector detector = new FileTypeDetector();

    @Test
    void shouldDetectPdfByContent() throws Exception {
        FileTypeDetector.DetectedFileInfo info = detector.detect("lesson.pdf", new ByteArrayInputStream(createPdfBytes()));

        assertThat(info.getDetectedType()).isEqualTo(ResourceFileDetectedType.PDF);
        assertThat(info.getMimeType()).isEqualTo("application/pdf");
    }

    @Test
    void shouldDetectImageByContent() throws Exception {
        FileTypeDetector.DetectedFileInfo info = detector.detect("cover.png", new ByteArrayInputStream(createPngBytes()));

        assertThat(info.getDetectedType()).isEqualTo(ResourceFileDetectedType.IMAGE);
        assertThat(info.getMimeType()).startsWith("image/");
    }

    @Test
    void shouldDetectZipByContent() throws Exception {
        FileTypeDetector.DetectedFileInfo info = detector.detect("bundle.zip", new ByteArrayInputStream(createZipBytes()));

        assertThat(info.getDetectedType()).isEqualTo(ResourceFileDetectedType.ZIP);
        assertThat(info.getMimeType()).contains("zip");
    }

    private byte[] createPdfBytes() throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.addPage(new PDPage());
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createPngBytes() throws IOException {
        BufferedImage image = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createZipBytes() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry("readme.txt"));
            zipOutputStream.write("hello".getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }
}
