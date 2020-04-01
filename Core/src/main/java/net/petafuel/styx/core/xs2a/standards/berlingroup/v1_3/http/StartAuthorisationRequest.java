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

public class StartAuthorisationRequest extends XS2AAuthorisationRequest {
    public static final String CS_START_AUTHORISATIONS = "/v1/consents/%s/authorisations";
    public static final String PIS_START_AUTHORISATIONS = "/v1/%s/%s/%s/authorisations";
    private PSUData psuData;

    public StartAuthorisationRequest(PSU psu, PSUData psuData, PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        super(paymentService, paymentProduct, paymentId);
        setPsu(psu);
        this.psuData = psuData;
    }

    public StartAuthorisationRequest(PSUData psuData, String consentId) {
        super(consentId);
        this.psuData = psuData;
    }

    public PSUData getPsuData() {
        return psuData;
    }

    public void setPsuData(PSUData psuData) {
        this.psuData = psuData;
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
            return String.format(PIS_START_AUTHORISATIONS,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId());

        } else {
            return String.format(CS_START_AUTHORISATIONS, getConsentId());
        }
    }
}
