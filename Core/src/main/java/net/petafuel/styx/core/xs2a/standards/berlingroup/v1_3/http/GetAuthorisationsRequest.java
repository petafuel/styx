package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import javax.json.Json;
import javax.json.bind.JsonbConfig;
import java.util.Optional;

public class GetAuthorisationsRequest extends XS2AAuthorisationRequest {
    public static final String CS_GET_AUTHORISATIONS = "/v1/consents/%s/authorisations";
    public static final String PIS_GET_AUTHORISATIONS = "/v1/%s/%s/%s/authorisations";


    public GetAuthorisationsRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        super(paymentService, paymentProduct, paymentId);
    }

    public GetAuthorisationsRequest(String consentId) {
        super(consentId);
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
            return String.format(PIS_GET_AUTHORISATIONS,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId());
        } else {
            return String.format(CS_GET_AUTHORISATIONS, getConsentId());
        }
    }
}
