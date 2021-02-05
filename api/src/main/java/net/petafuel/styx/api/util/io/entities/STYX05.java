package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;

/**
 * Replace the code challenge placeholder from the link in the response with the value of the placeholder
 */
public class STYX05 extends ApplicableImplementerOption {

    private static final String IO = "STYX05";

    public STYX05(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public void apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        if (xs2AResponse instanceof StrongAuthenticatableResource) {

            Boolean optionRequired = ioParser.getOption(IO, IOParser.Option.REQUIRED);
            if (optionRequired != null && optionRequired) {
                StrongAuthenticatableResource response = (StrongAuthenticatableResource) xs2AResponse;
                String authorizationLink = response.getLinks().getScaOAuth().getUrl();
                OAuthSession oAuthSession = OAuthSession.start();
                oAuthSession.setScope(SCAHandler.getQueryParameterValue(authorizationLink, "scope"));
                oAuthSession.setAuthorizationEndpoint(getBaseUrl(authorizationLink));
                boolean isSandbox = WebServer.isSandbox();
                Aspsp aspsp = ioParser.getAspsp();
                if (isSandbox) {
                    oAuthSession.setTokenEndpoint(aspsp.getSandboxUrl().getPreauthTokenEndpoint());
                } else {
                    oAuthSession.setTokenEndpoint(aspsp.getProductionUrl().getPreauthTokenEndpoint());
                }

                PersistentOAuthSession.create(oAuthSession);
                String generatedCodeChallenge = OAuthService.getCodeChallengeFromState(oAuthSession.getState());
                String codeChallengePlaceholder = SCAHandler.getQueryParameterValue(authorizationLink, "code_challenge");
                authorizationLink = authorizationLink.replace(codeChallengePlaceholder, generatedCodeChallenge) + "&state=" + oAuthSession.getState();
                response.getLinks().setScaOAuth(new Links.Href(authorizationLink, LinkType.SCA_REDIRECT));
            }
        }
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_RESPONSE;
    }

    private String getBaseUrl(String url) {
        int index = url.indexOf('?');
        return  url.substring(0, index);
    }
}
