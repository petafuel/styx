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

    @Test
    void testParseClientIpFromForwardedIPs() {
        Assertions.assertEquals("1.2.3.4", Sanitizer.parseClientIpFromForwardedIPs("1.2.3.4, 1.2.3.5, 1.2.3.6"));
        Assertions.assertNull(Sanitizer.parseClientIpFromForwardedIPs(""));
        Assertions.assertNull(Sanitizer.parseClientIpFromForwardedIPs(null));
    }
}