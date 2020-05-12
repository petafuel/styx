package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.rest.StyxFilterPriorites;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.persistence.PersistenceEmptyResultSetException;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.OAuthTokenExpiredException;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.oauth.http.RefreshTokenRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Priority(StyxFilterPriorites.XS2ASTANDARD_DEPENDENT)
@AcceptsPreStepAuth
public class PreAuthAccessFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(PreAuthAccessFilter.class);
    private static final String PRE_AUTH_ID = "preAuthId";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        XS2AStandard xs2AStandard = (XS2AStandard) containerRequestContext.getProperty(XS2AStandard.class.getName());
        IOParser ioParser = new IOParser(xs2AStandard.getAspsp());
        ImplementerOption ioPreAuthRequired = ioParser.get("IO6");

        if (ioPreAuthRequired != null && ioPreAuthRequired.getOptions().get(IOParser.Option.REQUIRED).getAsBoolean()) {
            LOG.info("ASPSP bic={} requires pre-step", xs2AStandard.getAspsp().getBic());
            //preauth is available and required for this bank -> check if preauth id is present
            String preAuthId = containerRequestContext.getHeaderString(PRE_AUTH_ID);
            if (preAuthId == null || "".equals(preAuthId)) {
                throw new StyxException(new ResponseEntity("The requested aspsps requires a pre-step authorisation, preAuthId Header is missing", ResponseConstant.STYX_PREAUTH_HEADER_REQUIRED, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
            }

            try {
                OAuthSession oAuthSession = PersistentOAuthSession.get(preAuthId);

                if (oAuthSession.getAccessTokenExpiresAt().before(new Date())) {
                    if (oAuthSession.getRefreshTokenExpiresAt().after(new Date())) {
                       oAuthSession = refreshToken(oAuthSession);
                    } else {
                        throw new OAuthTokenExpiredException(OAuthTokenExpiredException.MESSAGE);
                    }
                }

                //Add the Authorization: <type> <credentials> header to the request context so we can use it later on demand

                Map<String, String> additionalHeaders = new HashMap<>();
                additionalHeaders.put(XS2AHeader.AUTHORIZATION, oAuthSession.getTokenType() + " " + oAuthSession.getAccessToken());
                containerRequestContext.setProperty(PreAuthAccessFilter.class.getName(), additionalHeaders);

            } catch (PersistenceEmptyResultSetException noOauthSessionFound) {
                throw new StyxException(new ResponseEntity("There was no valid pre-step authorisation found for the specified preAuthId", ResponseConstant.STYX_PREAUTH_NOT_AVAILABLE, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
            } catch (OAuthTokenExpiredException tokenExpired) {
                throw new StyxException(new ResponseEntity(tokenExpired.getMessage(), ResponseConstant.STYX_PREAUTH_EXPIRED, ResponseCategory.ERROR, ResponseOrigin.CLIENT));
            }
        }
    }

    private OAuthSession refreshToken(OAuthSession oAuthSession) throws OAuthTokenExpiredException {
        String preAuthId = oAuthSession.getState();
        RefreshTokenRequest request = new RefreshTokenRequest(oAuthSession.getRefreshToken());
        OAuthService service = new OAuthService();
        try {
            oAuthSession = service.tokenRequest(oAuthSession.getTokenEndpoint(), request);
            oAuthSession.setState(preAuthId);
            PersistentOAuthSession.update(oAuthSession);
            return oAuthSession;
        } catch (BankRequestFailedException expiredToken) {
            throw new OAuthTokenExpiredException(OAuthTokenExpiredException.MESSAGE);
        }
    }
}
