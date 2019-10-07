package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.exceptions.CertificateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class CertificateManager {
    private static final Logger LOG = LogManager.getLogger(CertificateManager.class);
    private static final String KEYSTORE_PATH = "keystore.path";
    private static final String KEYSTORE_PASS_PATH = "keystore.password.path";
    private static final String KEYSTORE_STYX_ALIAS = "keystore.styxalias";
    private static CertificateManager singletonInstance;
    private KeyStore clientKeyStore;
    private String keyStoreStyxAlias;
    private char[] password;

    private CertificateManager() {
        Properties config = new Properties();
        try (InputStream in = CertificateManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            config.load(in);
        } catch (IOException e) {
            LOG.error("Error while loading properties: " + e.getMessage());
            throw new CertificateException("Error while loading certificate properties: " + e.getMessage());
        }
        String keyStorePath = config.getProperty(KEYSTORE_PATH);
        this.keyStoreStyxAlias = config.getProperty(KEYSTORE_STYX_ALIAS);
        try {
            this.password = new String(Files.readAllBytes(Paths.get(config.getProperty(KEYSTORE_PASS_PATH))), StandardCharsets.UTF_8).toCharArray();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new CertificateException("Unable to load password for keystore: " + e.getMessage());
        }

        try {
            this.clientKeyStore = KeyStore.getInstance("PKCS12");
            this.clientKeyStore.load(new FileInputStream(keyStorePath), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | java.security.cert.CertificateException e) {
            LOG.error("Something went wrong while loading keystore file: " + e.getMessage());
            throw new CertificateException("Something went wrong while loading keystore file: " + e.getMessage());
        } catch (FileNotFoundException e) {
            LOG.error("Unable to find keystore file: " + e.getMessage());
            throw new CertificateException("Unable to find keystore file: " + e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    /**
     * Get the current static CertificateManager object
     * @return CertificateManager
     */
    public static CertificateManager getInstance() {
        if (CertificateManager.singletonInstance == null) {
            CertificateManager.singletonInstance = new CertificateManager();
        }
        return CertificateManager.singletonInstance;
    }

    /**
     * Return the client certificate specified by the config.properties property keystore.styxalias
     * @return X509Certificate
     */
    public X509Certificate getCertificate() {
        try {
            return (X509Certificate) this.clientKeyStore.getCertificate(this.keyStoreStyxAlias);
        } catch (KeyStoreException e) {
            LOG.error("Unable to get styx certificate from keystore via alias " + this.keyStoreStyxAlias + " : " + e.getMessage());
            throw new CertificateException("Unable to get styx certificate from keystore: " + e.getMessage());
        }
    }

    /**
     * Returns the sslcontext with initialized client certificate from our keystore
     * @return SSLContext
     */
    public SSLContext getSSLContext() {
        SSLContext sslContext;
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(this.clientKeyStore, this.password);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManagers, null, new SecureRandom());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException e) {
            LOG.error(e.getMessage());
            throw new CertificateException("Unable to get sslcontext: " + e.getMessage());
        }
        return sslContext;
    }

    /**
     * Extracts the key from our keystore, also dependend on the alias chosen for the crt within the keystore
     * @return PrivateKey
     */
    public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) this.clientKeyStore.getKey(this.keyStoreStyxAlias, this.password);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            LOG.error("Unable to get styx private key from keystore via alias " + this.keyStoreStyxAlias + " : " + e.getMessage());
            throw new CertificateException("Unable to get styx private key from keystore: " + e.getMessage());
        }
    }
}
