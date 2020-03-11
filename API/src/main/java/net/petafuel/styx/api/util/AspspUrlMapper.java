package net.petafuel.styx.api.util;

import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.SCA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

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
    private static final String PIS_GET_SELF = "/v1/%s/%s/%s";
    private static final String CS_GET_STATUS = "/v1/consents/%s/status";
    private static final String CS_GET_SELF = "/v1/consents/%s";

    private PaymentService paymentService;
    private PaymentProduct paymentProduct;
    private String paymentId;
    private String authorisationId;
    private String consentId;
    private Boolean isPIS;
    private String proxySchema = System.getProperty(ApiProperties.STYX_PROXY_SCHEMA);
    private String proxyHostname = System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME);
    private int proxyPort = System.getProperty(ApiProperties.STYX_PROXY_PORT, null) != null ? Integer.parseInt(System.getProperty(ApiProperties.STYX_PROXY_PORT)) : -1;

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
            String route = null;
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
                    break;
                case UPDATE_PSU_IDENTIFICATION:
                case UPDATE_PSU_AUTHENTICATION:
                case UPDATE_ENCRYPTED_PSU_AUTHENTICATION:
                case UPDATE_ADDITIONAL_PSU_AUTHENTICATION:
                case UPDATE_ENCRYPTED_ADDITIONAL_PSU_AUTHENTICATION:
                case AUTHORISE_TRANSACTION:
                case SCA_STATUS:
                    if (isPIS) {
                        route = String.format(PIS_UPDATE_AUTHORISATION, paymentService.getValue(), paymentProduct.getValue(), paymentId, authorisationId);
                    } else {
                        route = String.format(CS_UPDATE_AUTHORISATION, consentId, authorisationId);
                    }
                    break;
                default:
                    //Keep url as is
                    break;
            }

            Optional<URL> styxWrapperUrl = getMappedURL(route);
            styxWrapperUrl.ifPresent(presentValue -> entry.setValue(presentValue.toString()));
        });

        //Always add Status and Self links
        String getStatus;
        String getSelf;
        if (isPIS) {
            getStatus = String.format(PIS_GET_STATUS, paymentService.getValue(), paymentProduct.getValue(), paymentId);
            getSelf = String.format(PIS_GET_SELF, paymentService.getValue(), paymentProduct.getValue(), paymentId);
        } else {
            getStatus = String.format(CS_GET_STATUS, consentId);
            getSelf = String.format(CS_GET_SELF, consentId);
        }
        Optional<URL> styxWrapperUrlStatus = getMappedURL(getStatus);
        Optional<URL> styxWrapperUrlSelf = getMappedURL(getSelf);
        styxWrapperUrlStatus.ifPresent(presentValue -> links.put(SCA.LinkType.STATUS, presentValue.toString()));
        styxWrapperUrlSelf.ifPresent(presentValue -> links.put(SCA.LinkType.SELF, presentValue.toString()));
    }

    //Only map the url if its mappable(route!=null) or if there was no error
    private Optional<URL> getMappedURL(String route) {
        if (route == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new URL(proxySchema, proxyHostname, proxyPort, route));
        } catch (MalformedURLException e) {
            LOG.error("Unable to wrap aspsp rest response links object to styx urls", e);
            return Optional.empty();
        }
    }
}
