package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import javax.json.Json;
import javax.json.bind.JsonbConfig;
import java.util.Optional;

public class GetAuthorisationRequest extends XS2ARequest {
    private String paymentService;
    private String paymentProduct;
    private String paymentId;
    private String consentId;
    private String type;

    public GetAuthorisationRequest(String paymentService, String paymentProduct, String paymentId) {
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.paymentId = paymentId;
        this.type = "PIS";
    }

    public GetAuthorisationRequest(String consentId) {
        this.consentId = consentId;
        this.type = "AIS";
    }

    public String getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(String paymentService) {
        this.paymentService = paymentService;
    }

    public String getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(String paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getConsentId() {
        return consentId;
    }

    @Override
    public void setConsentId(String consentId) {
        this.consentId = consentId;
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
}
