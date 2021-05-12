package net.petafuel.styx.api.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SanitizerUnitTest {

    @Test
    void replaceEscSeq() {
        String text = "newline\ntab\treturn\r";
        Assertions.assertEquals("newline_tab_return_", Sanitizer.replaceEscSeq(text));
    }

    @Test
    void testNullValue() {
        Assertions.assertNull(Sanitizer.replaceEscSeq(null));
    }
}