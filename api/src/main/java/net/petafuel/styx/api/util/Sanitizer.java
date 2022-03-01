package net.petafuel.styx.api.util;

public final class Sanitizer {
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

    /**
     * parse X-FORWARDED-FOR header in format (ip, ip, ip)
     *
     * @param forwardedIps content of X-FORWARDED-FOR header
     * @return clientIp or null if forwardedIps is null or empty
     */
    public static String parseClientIpFromForwardedIPs(String forwardedIps) {
        if (forwardedIps != null && !forwardedIps.isEmpty()) {
            String[] ips = forwardedIps.split(",");
            if (ips.length > 0) {
                return ips[0].trim();
            }
        }
        return null;
    }
}
