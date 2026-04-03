package com.teacherresourcehub.common.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceCodeGeneratorTest {

    @Test
    void shouldGenerateCodeWithCategoryCodeDateAndSequence() {
        String result = ResourceCodeGenerator.generate("JZH", LocalDate.of(2026, 4, 3), 1);

        assertEquals("RES-JZH-20260403-0001", result);
    }

    @Test
    void shouldNormalizeCategoryCodeToUpperCase() {
        String result = ResourceCodeGenerator.generate("bhz", LocalDate.of(2026, 4, 3), 27);

        assertEquals("RES-BHZ-20260403-0027", result);
    }

    @Test
    void shouldRejectBlankCategoryCode() {
        assertThrows(IllegalArgumentException.class,
                () -> ResourceCodeGenerator.generate(" ", LocalDate.of(2026, 4, 3), 1));
    }
}
