package net.petafuel.styx.api;

import net.petafuel.styx.api.exception.HealthCheckException;
import net.petafuel.styx.api.util.HealthCheck;
import net.petafuel.styx.core.xs2a.utils.CertificateManager;
import net.petafuel.styx.core.xs2a.utils.Config;
import net.petafuel.styx.keepalive.threads.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.petafuel.styx.keepalive.contracts.Properties.MANAGER_PROBE_FREQUENCY;
import static net.petafuel.styx.keepalive.contracts.Properties.MANAGER_PROBE_INITIAL_DELAY;
import static net.petafuel.styx.keepalive.contracts.Properties.MANAGER_USE_RECOVERY;
import static net.petafuel.styx.keepalive.contracts.Properties.PAYMENT_STATUS_HOOK_SERVICE;
import static net.petafuel.styx.keepalive.contracts.Properties.PAYMENT_STATUS_MAX_REQUEST_FAILURES;
import static net.petafuel.styx.keepalive.contracts.Properties.PAYMENT_STATUS_POLL_MAX_EXECUTION_TIME;
import static net.petafuel.styx.keepalive.contracts.Properties.PAYMENT_STATUS_TIMEOUT_BETWEEN_RETRIES;
import static net.petafuel.styx.keepalive.contracts.Properties.TASKS_CONSENTPOLL_AMOUNT_RETRIES;
import static net.petafuel.styx.keepalive.contracts.Properties.TASKS_CONSENTPOLL_TIMEOUT_BETWEEN_RETRIES;
import static net.petafuel.styx.keepalive.contracts.Properties.THREADS_COREWORKER_MAX_AMOUNT;
import static net.petafuel.styx.keepalive.contracts.Properties.THREADS_COREWORKER_MIN_AMOUNT;
import static net.petafuel.styx.keepalive.contracts.Properties.THREADS_COREWORKER_SPAWN_THRESHOLD;

public class StartUp {

    private static final Logger LOG = LogManager.getLogger(StartUp.class);

    public static void main(String[] args) throws HealthCheckException {
        LOG.info("Starting Styx");
        LOG.info("Loading api.properties and core.properties");
        //load api.properties
        PropertyReader propertyReader = new PropertyReader();
        propertyReader.loadProperties();
        //load core.properties on application startup
        Config.getInstance();

        //checking if the configuration is sufficient
        prepareConfigurationValidations();
        HealthCheck.validateConfiguration();

        LOG.info("Initializing CertificateManager Qwac and QSeal ");
        //constructor call to load qsealc and qwac data into static memory before the http server was started
        CertificateManager.getInstance();

        WebServer server = new WebServer();
        LOG.info("Starting Keep-Alive ThreadManager");
        ThreadManager.getInstance().start();
        try {
            LOG.info("Starting HTTP Server for REST Service");
            server.startHttpServer();
        } catch (Exception e) {
            LOG.error("Server could not be started", e);
            server.stopHttpServer();
        }
    }

    private static void prepareConfigurationValidations() {
        /*
         * api.properties checks
         */
        HealthCheck.addCheck("styx.api.ip", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("styx.api.port", HealthCheck.CHECK.INTEGER);
        HealthCheck.addCheck("styx.api.sad.sandbox.enabled", HealthCheck.CHECK.BOOL);
        HealthCheck.addCheck("styx.proxy.enabled", HealthCheck.CHECK.BOOL);
        HealthCheck.addCheck("styx.proxy.hostname", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("styx.proxy.port", HealthCheck.CHECK.INTEGER);
        HealthCheck.addCheck("styx.proxy.schema", HealthCheck.CHECK.STRING);
        HealthCheck.addOptionalCheck("token.access.unusedlifetime", HealthCheck.CHECK.INTEGER);

        /*
         * core.properties checks
         */
        //Core properties
        HealthCheck.addCheck("keystore.path", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("keystore.path", HealthCheck.CHECK.FILE_EXISTS);
        HealthCheck.addCheck("keystore.password.path", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("keystore.password.path", HealthCheck.CHECK.FILE_EXISTS);
        HealthCheck.addCheck("keystore.seal.path", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("keystore.seal.path", HealthCheck.CHECK.FILE_EXISTS);
        HealthCheck.addCheck("keystore.seal.password.path", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("keystore.seal.password.path", HealthCheck.CHECK.FILE_EXISTS);
        HealthCheck.addCheck("keystore.client_id", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("keystore.styxalias", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("styx.redirect.baseurl", HealthCheck.CHECK.STRING);
        HealthCheck.addCheck("persistence.dbname", HealthCheck.CHECK.STRING);

        //Keep-Alive properties
        HealthCheck.addOptionalCheck(THREADS_COREWORKER_MIN_AMOUNT, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(THREADS_COREWORKER_MAX_AMOUNT, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(THREADS_COREWORKER_SPAWN_THRESHOLD, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(MANAGER_USE_RECOVERY, HealthCheck.CHECK.BOOL);
        HealthCheck.addOptionalCheck(MANAGER_PROBE_FREQUENCY, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(MANAGER_PROBE_INITIAL_DELAY, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(TASKS_CONSENTPOLL_AMOUNT_RETRIES, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(TASKS_CONSENTPOLL_TIMEOUT_BETWEEN_RETRIES, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(PAYMENT_STATUS_POLL_MAX_EXECUTION_TIME, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(PAYMENT_STATUS_HOOK_SERVICE, HealthCheck.CHECK.STRING);
        HealthCheck.addOptionalCheck(PAYMENT_STATUS_MAX_REQUEST_FAILURES, HealthCheck.CHECK.INTEGER);
        HealthCheck.addOptionalCheck(PAYMENT_STATUS_TIMEOUT_BETWEEN_RETRIES, HealthCheck.CHECK.INTEGER);
    }
}
