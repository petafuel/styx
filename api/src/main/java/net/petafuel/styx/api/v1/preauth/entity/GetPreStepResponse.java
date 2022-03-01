package net.petafuel.styx.api.v1.preauth.entity;

import net.petafuel.styx.api.ioprocessing.options.STYX02;
import net.petafuel.styx.api.util.DateConverter;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.annotation.JsonbNillable;

@JsonbNillable
public class GetPreStepResponse {
    private static final Logger LOG = LogManager.getLogger(GetPreStepResponse.class);

    private String preAuthId;
    private String scope;
    private String psuId;
    private String validatedAt;
    private String validUntil;

    /**
     * Casting Constructor
     * @param oAuthSession OAuthSession object from which a new GetPreStepResponse will ne created
     */
    public GetPreStepResponse(OAuthSession oAuthSession){
        preAuthId = oAuthSession.getId().toString();
        scope = oAuthSession.getScope();
        String accessToken = oAuthSession.getAccessToken();
        if(accessToken != null) {
            STYX02 ioStyx02 = new STYX02(null);
            try {
                psuId = ioStyx02.extractPsuId(oAuthSession.getAccessToken());
            } catch (ImplementerOptionException e) {
                LOG.warn(e.getMessage());
            }
        }
        validatedAt = oAuthSession.getAuthorizedAt() != null ? DateConverter.toISOFormatUTC(oAuthSession.getAuthorizedAt()) : null;
        validUntil = oAuthSession.getRefreshTokenExpiresAt() != null ? DateConverter.toISOFormatUTC(oAuthSession.getRefreshTokenExpiresAt()) : null;

    }

    public String getPreAuthId() {
        return preAuthId;
    }

    public void setPreAuthId(String preAuthId) {
        this.preAuthId = preAuthId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPsuId() {
        return psuId;
    }

    public void setPsuId(String psuId) {
        this.psuId = psuId;
    }

    public String getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(String validatedAt) {
        this.validatedAt = validatedAt;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }
}
