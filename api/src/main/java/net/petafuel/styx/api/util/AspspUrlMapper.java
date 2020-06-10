package net.petafuel.styx.api.util;

import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
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

    private static final String AIS_GET_TRANSACTIONS = "/v1/accounts/%s/transactions";
    private static final String AIS_GET_BALANCES = "/v1/accounts/%s/balances";

    private PaymentService paymentService;
    private PaymentProduct paymentProduct;
    private String paymentId;
    private String authorisationId;
    private String consentId;
    private String accountId;
    private final boolean isPIS;
    private final String proxySchema = System.getProperty(ApiProperties.STYX_PROXY_SCHEMA);
    private final String proxyHostname = System.getProperty(ApiProperties.STYX_PROXY_HOSTNAME);
    private final int proxyPort = System.getProperty(ApiProperties.STYX_PROXY_PORT, null) != null ? Integer.parseInt(System.getProperty(ApiProperties.STYX_PROXY_PORT)) : -1;

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

    public AspspUrlMapper(String accountId) {
        this.accountId = accountId;
        this.isPIS = false;
    }

    public static String extractAuthorisationId(Links links) {
        for (Map.Entry<LinkType, Links.Href> entry : links.getUrlMapping().entrySet()) {
            if (entry.getValue().getUrl().contains("authorisations/")) {
                String[] routeParts = entry.getValue().getUrl().split("/");
                return routeParts[routeParts.length - 1];
            }
        }
        return null;
    }

    //Links need to be adapted depening on multiple cases
    @SuppressWarnings("squid:S3776")
    public Links map(Links links) {
        if (!Boolean.parseBoolean(System.getProperty(ApiProperties.STYX_PROXY_ENABLED))) {
            return links;
        }
        if (links == null) {
            //If the aspsp did not deliver any links within the HAL protocol create a new links object so we can at least add self and status
            links = new Links();
        }
        //copy object for lambda expression
        Links finalLinks = links;

        finalLinks.getUrlMapping().entrySet().parallelStream().forEach(entry -> {
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
                case SELECT_AUTHENTICATION_METHOD:
                case AUTHORISE_TRANSACTION:
                case SCA_STATUS:
                    if (authorisationId == null || Objects.equals(authorisationId, "")) {
                        authorisationId = extractAuthorisationId(finalLinks);
                    }
                    if (isPIS) {
                        route = String.format(PIS_UPDATE_AUTHORISATION, paymentService.getValue(), paymentProduct.getValue(), paymentId, authorisationId);
                    } else {
                        route = String.format(CS_UPDATE_AUTHORISATION, consentId, authorisationId);
                    }
                    break;
                case BALANCES:
                    route = String.format(AIS_GET_BALANCES, accountId);
                    break;
                case TRANSACTIONS:
                    route = String.format(AIS_GET_TRANSACTIONS, accountId);
                    break;
                default:
                    //Keep url as is
                    break;
            }

            Optional<URL> styxWrapperUrl = getMappedURL(route);
            styxWrapperUrl.ifPresent(presentValue -> entry.getValue().setUrl(presentValue.toString()));
        });

        //Always add Status and Self links
        String getStatus = null;
        String getSelf = null;
        if (isPIS) {
            getStatus = String.format(PIS_GET_STATUS, paymentService.getValue(), paymentProduct.getValue(), paymentId);
            getSelf = String.format(PIS_GET_SELF, paymentService.getValue(), paymentProduct.getValue(), paymentId);
        } else if (consentId != null) {
            getStatus = String.format(CS_GET_STATUS, consentId);
            getSelf = String.format(CS_GET_SELF, consentId);
        }
        Optional<URL> styxWrapperUrlStatus = getMappedURL(getStatus);
        Optional<URL> styxWrapperUrlSelf = getMappedURL(getSelf);
        styxWrapperUrlStatus.ifPresent(presentValue -> finalLinks.setStatus(new Links.Href(presentValue.toString(), LinkType.STATUS)));
        styxWrapperUrlSelf.ifPresent(presentValue -> finalLinks.setSelf(new Links.Href(presentValue.toString(), LinkType.SELF)));
        return finalLinks;
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
