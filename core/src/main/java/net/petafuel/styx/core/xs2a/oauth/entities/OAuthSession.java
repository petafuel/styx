package net.petafuel.styx.core.xs2a.oauth.entities;

import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.serializers.OAuthSessionTypeAdapter;
import net.petafuel.styx.core.xs2a.oauth.serializers.SecondsToDateDeserializer;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.util.Date;
import java.util.UUID;

@JsonbTypeAdapter(OAuthSessionTypeAdapter.class)
public class OAuthSession {

    /**
     * also used as "preauthId" during the pre-step
     * during pre-step, id and xRequestId is equal
     */
    private UUID id;
    /**
     * the xRequestId which was used during the creation of the consent/payment
     */
    private UUID xRequestId;
    private String authorizationEndpoint;
    private String tokenEndpoint;
    @JsonbProperty("code_verifier")
    private String codeVerifier;
    private String state;
    private Date authorizedAt;
    private Date createdAt;
    @JsonbProperty("access_token")
    private String accessToken;
    @JsonbProperty("token_type")
    private String tokenType;
    @JsonbProperty("refresh_token")
    private String refreshToken;
    @JsonbTypeDeserializer(SecondsToDateDeserializer.class)
    @JsonbProperty("expires_in")
    private Date accessTokenExpiresAt;
    /**
     * This is normally set via OAuthSessionTypeAdapter
     *
     * @see net.petafuel.styx.core.xs2a.oauth.serializers.OAuthSessionTypeAdapter
     */
    private Date refreshTokenExpiresAt;
    private String scope;

    public OAuthSession(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public OAuthSession() {
    }

    public static OAuthSession start(UUID xRequestId) {
        OAuthSession oAuthSession = new OAuthSession();
        oAuthSession.setId(UUID.randomUUID());
        oAuthSession.setState(UUID.randomUUID().toString());
        oAuthSession.setCodeVerifier(OAuthService.generateCodeVerifier());
        oAuthSession.setxRequestId(xRequestId);
        return oAuthSession;
    }

    public UUID getxRequestId() {
        return xRequestId;
    }

    public void setxRequestId(UUID xRequestId) {
        this.xRequestId = xRequestId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(Date accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public Date getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(Date refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getAuthorizedAt() {
        return authorizedAt;
    }

    public void setAuthorizedAt(Date authorizedAt) {
        this.authorizedAt = authorizedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
