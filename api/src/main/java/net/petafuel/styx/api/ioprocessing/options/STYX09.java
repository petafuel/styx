package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.ioprocessing.IOParser;
import net.petafuel.styx.api.ioprocessing.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.ioprocessing.contracts.IOOrder;
import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.services.AccessTokenService;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.entities.AccessToken;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.http.AccessTokenRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ASPSP requires PSU-IP-Address
 * add psuIpAddress header to xs2aRequest if option is true
 */
public class STYX09 extends ApplicableImplementerOption {
    private static final Logger LOG = LogManager.getLogger(STYX09.class);

    public STYX09(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {

        Boolean optionRequired = ioParser.getOption(STYX09.class.getSimpleName(), IOParser.Option.REQUIRED);
        //return immediately if this options is not required
        if (optionRequired == null || !optionRequired) {
            return false;
        }

        AccessTokenService service = new AccessTokenService();

        String url;
        boolean isSandbox = WebServer.isSandbox();
        Aspsp aspsp = ioParser.getAspsp();
        if (isSandbox) {
            url = aspsp.getSandboxUrl().getCommonUrl();
        } else {
            url = aspsp.getProductionUrl().getCommonUrl();
        }

        AccessTokenRequest request = new AccessTokenRequest();
        AccessToken accessToken;
        try {
            accessToken = service.tokenRequest(url + "/oauth2/token", request);
        } catch (BankRequestFailedException e) {
            LOG.error("Error getting ing access token: {}", e.getMessage());
            ResponseEntity responseEntity = new ResponseEntity("Generating ING access token failed", ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX);
            throw new StyxException(responseEntity);
        }

        //add additional data to headers for further requests
        xs2ARequest.addHeader(XS2AHeader.AUTHORIZATION, "Bearer " + accessToken.getToken());
        xs2ARequest.addHeader(XS2AHeader.ING_CLIENT_ID, accessToken.getClientId());
        //just set the target to payment initiation for now => later we have to evaluate the http method and url based on the request so other calls (get payment info) will work too
        xs2ARequest.addHeader(XS2AHeader.REQUEST_TARGET, "post /v1/payments/sepa-credit-transfers");

        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
