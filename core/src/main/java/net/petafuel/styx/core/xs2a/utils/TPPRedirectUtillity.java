package net.petafuel.styx.core.xs2a.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class TPPRedirectUtillity {
    private static final Logger LOG = LogManager.getLogger(TPPRedirectUtillity.class);

    //Utillity class
    private TPPRedirectUtillity() {
    }

    /**
     * Creates a redirect url based on the styx.redirect.baseurl property
     *
     * @param path should contain a route which is available in the callbackresource of the api
     * @return
     */
    public static String getTPPRedirectFromConfig(String path) {
        return generateRedirectURL(System.getProperty(CoreProperties.STYX_REDIRECT_URL), path);
    }

    /**
     * @param baseUrl
     * @param path
     * @return
     */
    public static String generateRedirectURL(String baseUrl, String path) {
        try {
            return new URL(new URL(baseUrl), path).toExternalForm();
        } catch (MalformedURLException malformedURLException) {
            LOG.warn("Unable to create TPP Redirect URL from config baseUrl={}, path={}", baseUrl, path, malformedURLException);
            return Config.getInstance().getProperties().getProperty("styx.redirect.baseurl");
        }
    }
}
