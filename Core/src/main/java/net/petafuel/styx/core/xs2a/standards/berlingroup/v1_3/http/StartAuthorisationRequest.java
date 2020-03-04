package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSUData;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import java.io.StringReader;
import java.util.Optional;

public class StartAuthorisationRequest extends XS2ARequest {

    @JsonbProperty("psuData")
    private PSUData psuData;
    private String service;
    private int expectedResponseCode = 201;

    public StartAuthorisationRequest(String service) {
        super();
        this.service = service;
    }

    public PSUData getPsuData() {
        return psuData;
    }

    public void setPsuData(PSUData psuData) {
        this.psuData = psuData;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getExpectedResponseCode() {
        return expectedResponseCode;
    }

    public void setExpectedResponseCode(int expectedResponseCode) {
        this.expectedResponseCode = expectedResponseCode;
    }

    @Override
    public Optional<String> getRawBody() {
        JsonbConfig jsonbConfig = new JsonbConfig();
        jsonbConfig.withNullValues(false);
        try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {
            String requestBody;
            if (psuData == null) {
                requestBody = "{}";
            } else {
                JsonObject jsonObject = Json.createReader(new StringReader(jsonb.toJson(psuData))).readObject();
                requestBody = Json.createObjectBuilder().add("psuData", jsonObject).build().toString();
            }
            return Optional.ofNullable(requestBody);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
