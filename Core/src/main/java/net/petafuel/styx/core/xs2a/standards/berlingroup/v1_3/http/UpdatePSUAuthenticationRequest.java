package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PSUData;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.util.Optional;

public class UpdatePSUAuthenticationRequest extends XS2AAuthorisationRequest {
    // "/v1/{payment-service}/{payment.product}/{paymentId}/authorisations/{authorisationId}"
    public static final String PIS_UPDATE_PSU_DATA = "/v1/%s/%s/%s/authorisations/%s";
    // "/v1/consents/{consentId}/authorisations/{authorsationId}"
    public static final String CS_UPDATE_PSU_DATA = "/v1/consents/%s/authorisations/%s";
    private String authorisationId;
    private PSUData psuData;

    public UpdatePSUAuthenticationRequest(PSU psu, PSUData psuData, PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, String authorisationId) {
        super(paymentService, paymentProduct, paymentId);
        this.psuData = psuData;
        this.authorisationId = authorisationId;
        setPsu(psu);
    }

    public UpdatePSUAuthenticationRequest(PSU psu,PSUData psuData, String consentId, String authorisationId) {
        super(consentId);
        this.psuData = psuData;
        this.authorisationId = authorisationId;
        setPsu(psu);
    }

    @Override
    public Optional<String> getRawBody() {
        if (psuData == null) {
            return Optional.empty();
        }
        try (Jsonb jsonb = JsonbBuilder.create(); JsonReader jsonReader = Json.createReader(new StringReader(jsonb.toJson(psuData)))) {

            JsonObject psuDataJsonObject = jsonReader.readObject();
            return Optional.ofNullable(Json.createObjectBuilder().add("psuData", psuDataJsonObject).build().toString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public String getServiceURL() {
        if (isPIS()) {
            return String.format(PIS_UPDATE_PSU_DATA,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId(),
                    authorisationId);
        } else {
            return String.format(CS_UPDATE_PSU_DATA, getConsentId(), authorisationId);
        }
    }
}
