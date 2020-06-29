package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.util.Optional;

public class UpdatePSUAuthenticationRequest extends SCARequest {
    // "/v1/{payment-service}/{payment.product}/{paymentId}/authorisations/{authorisationId}"
    public static final String PIS_UPDATE_PSU_DATA = "/v1/%s/%s/%s/authorisations/%s";
    // "/v1/consents/{consentId}/authorisations/{authorsationId}"
    public static final String CS_UPDATE_PSU_DATA = "/v1/consents/%s/authorisations/%s";

    public UpdatePSUAuthenticationRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        super(paymentService, paymentProduct, paymentId);
    }

    public UpdatePSUAuthenticationRequest(String consentId) {
        super(consentId);
    }


    @Override
    public Optional<String> getRawBody() {
        if (getPsuData() == null) {
            return Optional.empty();
        }
        try (Jsonb jsonb = JsonbBuilder.create(); JsonReader jsonReader = Json.createReader(new StringReader(jsonb.toJson(getPsuData())))) {

            JsonObject psuDataJsonObject = jsonReader.readObject();
            return Optional.ofNullable(Json.createObjectBuilder().add("psuData", psuDataJsonObject).build().toString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public String getServicePath() {
        if (isPIS()) {
            return String.format(PIS_UPDATE_PSU_DATA,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId(),
                    getAuthroisationId());
        } else {
            return String.format(CS_UPDATE_PSU_DATA, getConsentId(), getAuthroisationId());
        }
    }
}
