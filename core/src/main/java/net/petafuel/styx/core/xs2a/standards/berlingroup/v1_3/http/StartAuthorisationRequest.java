package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.BasicService;
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

public class StartAuthorisationRequest extends SCARequest {
    public static final String CS_START_AUTHORISATIONS = "/v1/consents/%s/authorisations";
    public static final String PIS_START_AUTHORISATIONS = "/v1/%s/%s/%s/authorisations";

    public StartAuthorisationRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        super(paymentService, paymentProduct, paymentId);
    }

    public StartAuthorisationRequest(String consentId) {
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
    public BasicService.RequestType getHttpMethod() {
        return BasicService.RequestType.POST;
    }

    @Override
    public String getServicePath() {
        if (isPIS()) {
            return String.format(PIS_START_AUTHORISATIONS,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId());

        } else {
            return String.format(CS_START_AUTHORISATIONS, getConsentId());
        }
    }
}
