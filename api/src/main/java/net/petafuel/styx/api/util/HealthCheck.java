package net.petafuel.styx.api.util;

import net.petafuel.styx.api.exception.HealthCheckException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public final class HealthCheck {
    private static final Logger LOG = LogManager.getLogger(HealthCheck.class);
    private static final HashMap<String, CheckState> properties = new HashMap<>();
    private static boolean failed = false;

    private HealthCheck() {

    }

    private static void setFailed() {
        HealthCheck.failed = true;
    }

    /**
     * Will go through all defined properties by addCheck and addOptionalCheck to find out if anything did not pass validation
     *
     * @throws HealthCheckException in case a required configuration did not pass
     */
    public static void validateConfiguration() throws HealthCheckException {
        for (Map.Entry<String, CheckState> entry : HealthCheck.properties.entrySet()) {
            LOG.info("Checking '{}'...", entry.getKey());
            switch (entry.getValue().check) {
                case STRING:
                    processValidation(validateString(entry), entry.getKey(), entry.getValue().check);
                    break;
                case INTEGER:
                    processValidation(validateInteger(entry), entry.getKey(), entry.getValue().check);
                    break;
                case BOOL:
                    processValidation(validateBoolean(entry), entry.getKey(), entry.getValue().check);
                    break;
                case FILE_EXISTS:
                    processValidation(validateFileExists(entry), entry.getKey(), entry.getValue().check);
                    break;
                default:
                    LOG.error("Unknown CHECK type for '{}'", entry.getKey());
                    throw new HealthCheckException("Unable to validate config key " + entry.getKey());
            }

        }
        if (HealthCheck.failed) {
            throw new HealthCheckException("Healthcheck has detected a misconfiguration for Styx, please fix the preceding issues");
        } else {
            LOG.info("Configuration validation passed");
        }
    }

    private static boolean validateString(Map.Entry<String, CheckState> entry) {
        String prop = System.getProperty(entry.getKey());
        if (entry.getValue().isOptional && prop == null) {
            return true;
        }
        return prop != null && !"".equals(prop);
    }

    private static boolean validateInteger(Map.Entry<String, CheckState> entry) {
        String prop = System.getProperty(entry.getKey());
        if (entry.getValue().isOptional && prop == null) {
            return true;
        }
        try {
            Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            return false;
        }
        return !"".equals(prop);
    }

    private static boolean validateBoolean(Map.Entry<String, CheckState> entry) {
        String prop = System.getProperty(entry.getKey());
        if (entry.getValue().isOptional && prop == null) {
            return true;
        }
        return Boolean.TRUE.toString().equals(prop) || Boolean.FALSE.toString().equals(prop);
    }

    private static boolean validateFileExists(Map.Entry<String, CheckState> entry) {
        String prop = System.getProperty(entry.getKey());
        if (entry.getValue().isOptional && prop == null) {
            return true;
        }
        return Files.exists(new File(prop).toPath());
    }

    /**
     * strictly tests whether the system property exists and if the type is correct
     *
     * @param property
     * @param check
     */
    public static void addCheck(String property, CHECK check) {
        HealthCheck.properties.put(property, new CheckState(check));
    }

    /**
     * does the same as addCheck, but the actual check onto system property is only performed in case it is set within the system properties
     * in that case it is just validated as non optional properties
     *
     * @param property
     * @param check
     */
    public static void addOptionalCheck(String property, CHECK check) {
        HealthCheck.properties.put(property, new CheckState(check, true));
    }

    private static void processValidation(boolean passed, String property, CHECK check) {
        if (!passed) {
            LOG.error("'{}' failed in {} validation", property, check);
            HealthCheck.setFailed();
        } else {
            LOG.info("'{}' passed", property);
        }
    }

    public enum CHECK {
        STRING,
        INTEGER,
        BOOL,
        FILE_EXISTS
    }

    public static class CheckState {
        public final CHECK check;
        public final boolean isOptional;

        public CheckState(CHECK check, boolean isOptional) {
            this.check = check;
            this.isOptional = isOptional;
        }

        public CheckState(CHECK check) {
            this.check = check;
            this.isOptional = false;
        }
    }
}
