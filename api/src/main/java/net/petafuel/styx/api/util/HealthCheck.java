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
    private static final HashMap<String, CheckState> checks = new HashMap<>();
    private static boolean failed = false;

    private HealthCheck() {

    }

    /**
     * This will clear the internal checks map and frees the static available memory.
     * Will also reset the validation flag to "not failed"
     */
    public static void reset() {
        HealthCheck.failed = false;
        HealthCheck.checks.clear();
    }

    /**
     * Will mark the current Health check as failed. This is bound static to the class and therefore needs
     * to be reset if another checks should be performaned
     */
    private static void setFailed() {
        HealthCheck.failed = true;
    }

    /**
     * Will go through all defined properties by addCheck and addOptionalCheck to find out if anything did not pass validation
     *
     * @throws HealthCheckException in case a required configuration did not pass
     */
    public static void validateConfiguration() throws HealthCheckException {
        for (Map.Entry<String, CheckState> entry : HealthCheck.checks.entrySet()) {
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

    /**
     * Validates the given property path as String
     *
     * @param entry should be the current check entry
     * @return returns true if the prop was not null and not empty or if the check was optional and the property path is not within the system properties
     */
    private static boolean validateString(Map.Entry<String, CheckState> entry) {
        String prop = System.getProperty(entry.getKey());
        if (entry.getValue().isOptional && prop == null) {
            return true;
        }
        return prop != null && !"".equals(prop);
    }

    /**
     * Validates the given property path as Integer
     *
     * @param entry should be the current check entry
     * @return returns true of the prop was not empty and was parsable as Integer by Integer.parseInt. If the check was optional, it will return true if the prop path is not contained within the system propeties
     */
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

    /**
     * Validates a given property path as Boolean
     *
     * @param entry entry should be the current check entry
     * @return returns true if the system property value is "true" or "false"
     */
    private static boolean validateBoolean(Map.Entry<String, CheckState> entry) {
        String prop = System.getProperty(entry.getKey());
        if (entry.getValue().isOptional && prop == null) {
            return true;
        }
        return Boolean.TRUE.toString().equals(prop) || Boolean.FALSE.toString().equals(prop);
    }

    /**
     * Validates if the given property values actually resolves to a valid filepath and existing file
     *
     * @param entry should be the current check entry
     * @return returns true if the given property value does have a valid filepath in it and if the resulting file does exist
     */
    private static boolean validateFileExists(Map.Entry<String, CheckState> entry) {
        String prop = System.getProperty(entry.getKey());
        if (entry.getValue().isOptional && prop == null) {
            return true;
        }

        if (prop == null || "".equals(prop)) {
            return false;
        }

        return Files.exists(new File(prop).toPath());
    }

    /**
     * strictly tests whether the system property exists and if the type is correct
     *
     * @param property the property path to be checked
     * @param check    which check should be performed
     */
    public static void addCheck(String property, CHECK check) {
        HealthCheck.checks.put(property, new CheckState(check));
    }

    /**
     * does the same as addCheck, but the actual check onto system property is only performed in case it is set within the system properties
     * in that case it is just validated as non optional properties
     *
     * @param property the property path to be checked
     * @param check    which check should be performed
     */
    public static void addOptionalCheck(String property, CHECK check) {
        HealthCheck.checks.put(property, new CheckState(check, true));
    }

    /**
     * Logs and fails the test in cases a validation could not be passed
     *
     * @param passed   should contain the boolean result of a performed validation/check
     * @param property should be the system property name on which the validation/check was performed upon
     * @param check    should be the check which was peformed
     */
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
            this(check, false);
        }
    }
}
