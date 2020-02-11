package net.petafuel.styx.api.util;

import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.SCA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Map aspsp links for authorisation etc. to styx proxy urls
 */
public class AspspUrlMapper {
    private static final Logger LOG = LogManager.getLogger(AspspUrlMapper.class);

    private static final String PIS_CREATE_OR_GET_AUTHORISATION = "/v1/%s/%s/%s/authorisations";
    private static final String CS_CREATE_OR_GET_AUTHORISATION = "/v1/consents/%s/authorisations";
    private static final String PIS_UPDATE_AUTHORISATION = "/v1/%s/%s/%s/authorisations/%s";
    private static final String CS_UPDATE_AUTHORISATION = "/v1/consents/%s/authorisations/%s";

    private static final String PIS_GET_STATUS = "/v1/%s/%s/%s/status";
    private static final String CS_GET_STATUS = "/v1/consents/%s/status";
    private PaymentService paymentService;
    private PaymentProduct paymentProduct;
    private String paymentId;
    private String authorisationId;
    private String consentId;
    private Boolean isPIS;

    public AspspUrlMapper(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, String authorisationId) {
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.paymentId = paymentId;
        this.authorisationId = authorisationId;
        this.isPIS = true;
    }

    public AspspUrlMapper(String consentId, String authorisationId) {
        this.consentId = consentId;
        this.authorisationId = authorisationId;
        this.isPIS = false;
    }

    public void map(Map<SCA.LinkType, String> links) {
        if (!Boolean.parseBoolean(System.getProperty(ApiProperties.STYX_PROXY_ENABLED))) {
            return;
        }

        links.entrySet().parallelStream().forEach(entry -> {
            try {
                URL styxWrapperUrl = null;
                String route;
                switch (entry.getKey()) {
                    case AUTHORISATION_WITH_PSU_IDENTIFICATION:
                    case AUTHORISATION_WITH_PSU_AUTHENTICATION:
                    case AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION:
                    case AUTHORISATION_WITH_METHOD_SELECTION:
                        if (isPIS) {
                            route = String.format(PIS_CREATE_OR_GET_AUTHORISATION, paymentService.getValue(), paymentProduct.getValue(), paymentId);
                        } else {
                            route = String.format(CS_CREATE_OR_GET_AUTHORISATION, consentId);
                        }
                        styxWrapperUrl = new URL(System.getProperty(ApiProperties.STYX_PROXY_SCHEMA), System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME), Integer.parseInt(System.getProperty(ApiProperties.STYX_PROXY_PORT)), route);
                        break;
                    case UPDATE_PSU_IDENTIFICATION:
                    case UPDATE_PSU_AUTHENTICATION:
                    case UPDATE_ENCRYPTED_PSU_AUTHENTICATION:
                    case UPDATE_ADDITIONAL_PSU_AUTHENTICATION:
                    case UPDATE_ENCRYPTED_ADDITIONAL_PSU_AUTHENTICATION:
                    case SCA_STATUS:
                        if (isPIS) {
                            route = String.format(PIS_UPDATE_AUTHORISATION, paymentService.getValue(), paymentProduct.getValue(), paymentId, authorisationId);
                        } else {
                            route = String.format(CS_UPDATE_AUTHORISATION, consentId, authorisationId);
                        }
                        styxWrapperUrl = new URL(System.getProperty(ApiProperties.STYX_PROXY_SCHEMA), System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME), Integer.parseInt(System.getProperty(ApiProperties.STYX_PROXY_PORT)), route);
                        break;
                    case STATUS:
                        if (isPIS) {
                            route = String.format(PIS_GET_STATUS, paymentService.getValue(), paymentProduct.getValue(), paymentId);
                        } else {
                            route = String.format(CS_GET_STATUS, consentId);
                        }
                        styxWrapperUrl = new URL(System.getProperty(ApiProperties.STYX_PROXY_SCHEMA), System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME), Integer.parseInt(System.getProperty(ApiProperties.STYX_PROXY_PORT)), route);
                        break;
                    default:
                        styxWrapperUrl = new URL(entry.getValue());
                }
                entry.setValue(styxWrapperUrl.toString());
            } catch (MalformedURLException e) {
                LOG.error("Unable to wrap aspsp rest response links object to styx urls", e);
            }
        });

    }
}
