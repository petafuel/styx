package net.petafuel.styx.api.ioprocessing.options;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.ioprocessing.IOParser;
import net.petafuel.styx.core.ioprocessing.ApplicableImplementerOption;
import net.petafuel.styx.core.ioprocessing.IOOrder;
import net.petafuel.styx.core.ioprocessing.ImplementerOptionException;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.INGSigner;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.entities.AccessToken;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.http.AccessTokenRequest;
import net.petafuel.styx.core.xs2a.standards.ing.v1_0.services.AccessTokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ING specific Token handling
 * Carefull wenn using with banks initialized through SAD that do not use 
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

        Aspsp aspsp = ioParser.getAspsp();
        String url = Boolean.TRUE.equals(WebServer.isSandbox()) ? aspsp.getSandboxUrl().getCommonUrl() : aspsp.getProductionUrl().getCommonUrl();

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
        xs2ARequest.addHeader(INGSigner.ING_CLIENT_ID, accessToken.getClientId());

        String httpMethod = xs2ARequest.getHttpMethod().toString().toLowerCase();
        String servicePath = xs2ARequest.getServicePath();
        xs2ARequest.addHeader(INGSigner.REQUEST_TARGET, httpMethod + " " + servicePath);

        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
