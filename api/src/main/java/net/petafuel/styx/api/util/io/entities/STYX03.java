package net.petafuel.styx.api.util.io.entities;

import com.google.gson.JsonElement;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.StrongAuthenticatableResource;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;

/**
 * Replace the code challenge placeholder from the link in the response with the value of the placeholder
 */
public class STYX03 extends ApplicableImplementerOption {

    private static final String IO = "STYX03";

    public static String preauthId;

    public STYX03(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public void apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        if (xs2AResponse instanceof StrongAuthenticatableResource) {

            JsonElement optionRequired = ioParser.getOption(IO, IOParser.Option.REQUIRED);
            if (optionRequired != null && optionRequired.getAsBoolean()) {
                StrongAuthenticatableResource response = (StrongAuthenticatableResource) xs2AResponse;
                String redirectLink = response.getLinks().getScaRedirect().getUrl();
                String codeChallenge = OAuthService.getCodeChallengeFromState(STYX03.preauthId);
                redirectLink = redirectLink.replace("{code_challenge}", codeChallenge);
                response.getLinks().setScaRedirect(new Links.Href(redirectLink, LinkType.SCA_REDIRECT));
            }
        }
    }

    @Override
    public IOOrder order() {
        return IOOrder.PRE_RESPONSE;
    }
}
