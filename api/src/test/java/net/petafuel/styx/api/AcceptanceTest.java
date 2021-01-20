package net.petafuel.styx.api;

import io.restassured.RestAssured;
import net.petafuel.styx.api.util.ApiProperties;
import org.junit.jupiter.api.BeforeAll;

/**
 * Setup RestAssured for basic acceptance testing of api endpoints
 */
public interface AcceptanceTest {
    String PROPERTY_MASTER_TOKEN = "test.token.master";

    @BeforeAll
    static void setup() {
        new PropertyReader().loadProperties();
        RestAssured.baseURI = System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME, "https://preprod-styx.paycenter.de");
        RestAssured.port = Integer.parseInt(System.getProperty(ApiProperties.STYX_PROXY_PORT, "443"));
        System.out.println("AcceptanceTest Superclass @BeforeAll");
    }
}
