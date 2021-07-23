package net.petafuel.styx.core.xs2a.contracts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Inherited
public @interface XS2AHeader {
    String ACCEPT = "accept";
    String AUTHORIZATION = "authorization";
    String CONSENT_ID = "consent-id";
    String DATE = "date";
    String DIGEST = "digest";
    String PSU_CORPORATE_ID = "psu-corporate-id";
    String PSU_CORPORATE_ID_TYPE = "psu-corporate-id-type";
    String PSU_GEO_LOCATION = "psu-geo-location";
    String PSU_ID = "psu-id";
    String PSU_ID_TYPE = "psu-id-type";
    String PSU_IP_ADDRESS = "psu-ip-address";
    String PSU_IP_PORT = "psu-ip-port";
    String PSU_USER_AGENT = "psu-user-agent";
    String SIGNATURE = "signature";
    String TPP_NOK_REDIRECT_URI = "tpp-nok-redirect-uri";
    String TPP_REDIRECT_PREFERRED = "tpp-redirect-preferred";
    String TPP_REDIRECT_URL = "tpp-redirect-uri";
    String TPP_SIGNATURE_CERTIFICATE = "tpp-signature-certificate";
    String X_REQUEST_ID = "x-request-id";
    String ASPSP_SCA_APPROACH = "aspsp-sca-approach";
    String PSU_BIC = "psu-bic";
    String REDIRECT_PREFERRED = "redirectPreferred";
    String CONTENT_TYPE = "content-type";

    String value() default "";

    boolean nested() default false;
}