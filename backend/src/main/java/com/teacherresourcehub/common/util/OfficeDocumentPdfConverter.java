package com.teacherresourcehub.common.util;

import java.io.IOException;
import java.nio.file.Path;

public interface OfficeDocumentPdfConverter {

    Path convertToPdf(Path sourceFile, Path outputDirectory) throws IOException, InterruptedException;
}
