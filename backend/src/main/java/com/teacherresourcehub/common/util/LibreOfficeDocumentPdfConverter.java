package com.teacherresourcehub.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Component
public class LibreOfficeDocumentPdfConverter implements OfficeDocumentPdfConverter {

    private final String officeCommand;

    public LibreOfficeDocumentPdfConverter(@Value("${app.preview.office-command:${APP_PREVIEW_OFFICE_COMMAND:soffice}}") String officeCommand) {
        this.officeCommand = (officeCommand == null || officeCommand.isBlank()) ? "soffice" : officeCommand;
    }

    @Override
    public Path convertToPdf(Path sourceFile, Path outputDirectory) throws IOException, InterruptedException {
        Files.createDirectories(outputDirectory);
        cleanupExistingPdf(outputDirectory);

        ProcessBuilder processBuilder = new ProcessBuilder(
                officeCommand,
                "--headless",
                "--convert-to",
                "pdf",
                "--outdir",
                outputDirectory.toAbsolutePath().toString(),
                sourceFile.toAbsolutePath().toString()
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes()).trim();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Office 文件转 PDF 失败：" + (output.isBlank() ? "soffice 执行异常" : output));
        }

        try (Stream<Path> files = Files.list(outputDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".pdf"))
                    .sorted(Comparator.comparing(Path::toString))
                    .findFirst()
                    .orElseThrow(() -> new IOException("Office 文件转 PDF 失败：未生成 PDF 文件"));
        }
    }

    private void cleanupExistingPdf(Path outputDirectory) throws IOException {
        try (Stream<Path> files = Files.list(outputDirectory)) {
            List<Path> pdfFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".pdf"))
                    .toList();
            for (Path pdfFile : pdfFiles) {
                Files.deleteIfExists(pdfFile);
            }
        }
    }
}
