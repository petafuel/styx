package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;

import java.util.UUID;

/**
 * Replace the code challenge placeholder from the link in the response with the value of the placeholder
 */
public class STYX03 extends ApplicableImplementerOption {

    private static final String IO = "STYX03";

    private static UUID preauthId;

    public STYX03(IOParser ioParser) {
        super(ioParser);
    }

    public static UUID getPreauthId() {
        return preauthId;
    }

    public static void setPreauthId(UUID preauthId) {
        STYX03.preauthId = preauthId;
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        if (xs2AResponse instanceof StrongAuthenticatableResource) {

            Boolean optionRequired = ioParser.getOption(IO, IOParser.Option.REQUIRED);
            if (optionRequired != null && optionRequired) {
                StrongAuthenticatableResource response = (StrongAuthenticatableResource) xs2AResponse;
                String redirectLink = response.getLinks().getScaRedirect().getUrl();
                OAuthSession oAuthSession = PersistentOAuthSession.getById(STYX03.getPreauthId());
                String codeChallenge = OAuthService.getCodeChallengeFromState(oAuthSession.getState());
                redirectLink = redirectLink.replace("{code_challenge}", codeChallenge);
                response.getLinks().setScaRedirect(new Links.Href(redirectLink, LinkType.SCA_REDIRECT));
                return true;
            }
        }
        return false;
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_RESPONSE;
    }
}
