package net.petafuel.styx.api.service;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;

import javax.ws.rs.HeaderParam;

/**
 * SAD Wrapper as Service for API usage - <b>only use this in conjunction with the BICFilter to guarantee that the PSU-BIC
 * Header is contained in the client request</b>
 */
public class SADService {
    @HeaderParam(XS2AHeader.PSU_BIC)
    private String bic;
    private XS2AStandard xs2AStandard;

    public XS2AStandard getXs2AStandard() {
        try {
            if (xs2AStandard == null) {
                xs2AStandard = new SAD().getBankByBIC(bic, WebServer.isSandbox());
            }
        } catch (BankNotFoundException bicNotFound) {
            throw new StyxException(new ResponseEntity(bicNotFound.getMessage(), ResponseConstant.SAD_ASPSP_NOT_FOUND, ResponseCategory.ERROR, ResponseOrigin.STYX));
        } catch (BankLookupFailedException internalSADError) {
            throw new StyxException(new ResponseEntity("SAD was unable to initialize required Services", ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX), internalSADError);
        }
        return xs2AStandard;
    }

    public String getBic() {
        return bic;
    }
}
