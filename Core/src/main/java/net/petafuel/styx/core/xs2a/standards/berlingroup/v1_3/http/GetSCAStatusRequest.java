package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import javax.json.Json;
import javax.json.bind.JsonbConfig;
import java.util.Optional;

public class GetSCAStatusRequest extends XS2AAuthorisationRequest {
    public static final String PIS_GET_SCA_STATUS = "/v1/%s/%s/%s/authorisations/%s";
    public static final String CS_GET_SCA_STATUS = "/v1/consents/%s/authorisations/%s";
    private String authorisationId;

    public GetSCAStatusRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, String authorisationId) {
        super(paymentService, paymentProduct, paymentId);
        this.authorisationId = authorisationId;
    }

    public GetSCAStatusRequest(String consentId, String authorisationId) {
        super(consentId);
        this.authorisationId = authorisationId;

    }

    @Override
    public Optional<String> getRawBody() {
        JsonbConfig jsonbConfig = new JsonbConfig();
        jsonbConfig.withNullValues(false);
        try {
            String responseBody = Json.createObjectBuilder().toString();
            return Optional.ofNullable(responseBody);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public String getServiceURL() {
        if (isPIS()) {
            return String.format(PIS_GET_SCA_STATUS,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId(),
                    authorisationId);
        } else {
            return String.format(CS_GET_SCA_STATUS, getConsentId(), authorisationId);
        }
    }
}
