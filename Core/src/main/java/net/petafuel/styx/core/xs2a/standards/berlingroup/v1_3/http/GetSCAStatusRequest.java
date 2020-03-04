package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import javax.json.Json;
import javax.json.bind.JsonbConfig;
import java.util.Optional;

public class GetSCAStatusRequest extends XS2ARequest {
    private String paymentService;
    private String paymentProduct;
    private String paymentId;
    private String consentId;
    private String authorisationId;
    private String type;

    public GetSCAStatusRequest(String paymentService, String paymentProduct, String paymentId, String authorisationId) {
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
        this.paymentId = paymentId;
        this.authorisationId = authorisationId;
        this.type = "PIS";
    }

    public GetSCAStatusRequest(String consentId, String authorisationId) {
        this.consentId = consentId;
        this.authorisationId = authorisationId;
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

    @Override
    public String getConsentId() {
        return consentId;
    }

    @Override
    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
