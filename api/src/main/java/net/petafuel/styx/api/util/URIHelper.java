package net.petafuel.styx.api.util;

/**
 * Helper class for styx related config urls
 */
public class URIHelper {
    private URIHelper() {
    }

    public static String getLocalStyxURI() {
        return "http://" + System.getProperty(ApiProperties.STYX_API_IP) + ":" + System.getProperty(ApiProperties.STYX_API_PORT);
    }

    public static String getProxyStyxURI() {
        String baseUrl = System.getProperty(ApiProperties.STYX_PROXY_SCHEMA) + "://" +
                System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME);
        if (System.getProperty(ApiProperties.STYX_PROXY_PORT) != null) {
            baseUrl += ":" + System.getProperty(ApiProperties.STYX_PROXY_PORT);
        }
        return baseUrl;
    }
}
