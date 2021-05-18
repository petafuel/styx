package net.petafuel.styx.api.util;

public class Sanitizer {
    private Sanitizer() {
    }

    /**
     * replace escape sequences \n, \r and \t
     *
     * @param text text to be replaced, non destructive
     * @return sanitized text is returned
     */
    public static String replaceEscSeq(String text) {
        return text != null ? text.replaceAll("[\n\r\t]", "_") : null;
    }
}
