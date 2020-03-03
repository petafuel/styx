package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSUData;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Optional;

public class StartAuthorisationRequest extends XS2ARequest {

    @JsonbProperty("psuData")
    private PSUData psuData;
    private String service;

    public StartAuthorisationRequest() {
        super();
    }

    public StartAuthorisationRequest(PSUData psuData, String service) {
        super();
        this.service = service;
        this.psuData = psuData;
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

    @Override
    public Optional<String> getRawBody() {
        JsonbConfig jsonbConfig = new JsonbConfig();
        jsonbConfig.withNullValues(false);
        try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {
            JsonObjectBuilder builder = Json.createObjectBuilder().add("password", psuData.getPassword());
            String responseBody = Json.createObjectBuilder()
                    .add("psuData", builder).build().toString();
            return Optional.ofNullable(responseBody);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
