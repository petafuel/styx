package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.exceptions.CertificateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

/**
 * Handels read access revolving around the client certificate handed out by the (N)CA
 * <p>
 * <br>CertificateManager is done via Singleton implementation to avoid reading in the Keystore multiple times
 * <br>Keystore changes require an application restart
 * <br>The certificate within the keystore should be stored using an alias, this can be set within the
 * config.properties in the Core Maven Module
 * </p>
 *
 * @since 1.0-SNAPSHOT
 */
public class CertificateManager {
    private static final Logger LOG = LogManager.getLogger(CertificateManager.class);
    private static final String KEYSTORE_PATH = "keystore.path";
    private static final String KEYSTORE_PASS_PATH = "keystore.password.path";
    private static final String KEYSTORE_STYX_ALIAS = "keystore.styxalias";
    private static CertificateManager singletonInstance;
    private final KeyStore clientKeyStore;
    private final String keyStoreStyxAlias;
    private final char[] password;

    //Reading an invalid keystore results in an exception. The path needs to be configured correctly
    @SuppressWarnings("squid:S4797")
    private CertificateManager() {

        String keyStorePath = Config.getInstance().getProperties().getProperty(KEYSTORE_PATH);
        this.keyStoreStyxAlias = Config.getInstance().getProperties().getProperty(KEYSTORE_STYX_ALIAS);
        try {
            this.password = new String(Files.readAllBytes(
                    Paths.get(Config.getInstance().getProperties().getProperty(KEYSTORE_PASS_PATH))),
                    StandardCharsets.UTF_8)
                    .toCharArray();
        } catch (IOException e) {
            LOG.error(e);
            throw new CertificateException("Unable to load password for keystore: " + e.getMessage(), e);
        }

        try {
            this.clientKeyStore = KeyStore.getInstance("PKCS12");
            this.clientKeyStore.load(new FileInputStream(keyStorePath), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | java.security.cert.CertificateException e) {
            LOG.error("Something went wrong while loading keystore file: {}", e.getMessage(), e);
            throw new CertificateException("Something went wrong while loading keystore file: " + e.getMessage(), e);
        } catch (FileNotFoundException e) {
            LOG.error("Unable to find keystore file: {}", e.getMessage(), e);
            throw new CertificateException("Unable to find keystore file: " + e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new CertificateException("Password or format error in keystore: " + e.getMessage(), e);
        }

    }

    /**
     * Get the current static CertificateManager object
     *
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
     *
     * @return X509Certificate
     * @see IXS2AHttpSigner
     */
    public X509Certificate getCertificate() {
        try {
            return (X509Certificate) this.clientKeyStore.getCertificate(this.keyStoreStyxAlias);
        } catch (KeyStoreException e) {
            LOG.error("Unable to get styx certificate from keystore via alias {} : {}", this.keyStoreStyxAlias, e.getMessage(), e);
            throw new CertificateException("Unable to get styx certificate from keystore: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the sslcontext with initialized client certificate from our keystore
     *
     * @return SSLContext
     */
    public SSLContext getSSLContext() {
        SSLContext sslContext;
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(this.clientKeyStore, this.password);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagers, null, new SecureRandom());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException e) {
            LOG.error(e.getMessage(), e);
            throw new CertificateException("Unable to get sslcontext: " + e.getMessage(), e);
        }
        return sslContext;
    }


    /**
     * Extracts the key from our keystore, also dependend on the alias chosen for the crt within the keystore
     *
     * @return PrivateKey
     */
    public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) this.clientKeyStore.getKey(this.keyStoreStyxAlias, this.password);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            LOG.error("Unable to get styx private key from keystore via alias {} : {}", this.keyStoreStyxAlias, e.getMessage(), e);
            throw new CertificateException("Unable to get styx private key from keystore: " + e.getMessage(), e);
        }
    }
}
