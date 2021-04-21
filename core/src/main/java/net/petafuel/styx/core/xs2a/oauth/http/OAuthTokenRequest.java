package net.petafuel.styx.core.xs2a.oauth.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.utils.Config;

import javax.json.bind.annotation.JsonbProperty;

public abstract class OAuthTokenRequest extends XS2ARequest {
    @JsonbProperty("grant_type")
    private String grantType;
    @JsonbProperty("client_id")
    private String clientId = Config.getInstance().getProperties().getProperty("keystore.client_id");
    private boolean jsonBody = true;

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(boolean jsonBody) {
        this.jsonBody = jsonBody;
    }
}
