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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.HeaderParam;

/**
 * SAD Wrapper as Service for API usage - <b>only use this in conjunction with the BICFilter to guarantee that the PSU-BIC
 * Header is contained in the client request</b>
 */
public class SADService {
    private static final Logger LOG = LogManager.getLogger(SADService.class);

    @HeaderParam(XS2AHeader.PSU_BIC)
    private String bic;
    private XS2AStandard xs2AStandard;

    public XS2AStandard getXs2AStandard() {
        try {
            if (xs2AStandard == null) {
                xs2AStandard = new SAD().getBankByBIC(bic, WebServer.isSandbox());
                LOG.info("XS2AStandard successfully initialized. bic={}, aspspName={}, aspspId={}, aspspGroup={}, aspspGroupId={}, standard={}, standardVersion={}, ais={}, cs={}, pis={}, piis={}, availableOptions={}",
                        xs2AStandard.getAspsp().getBic(),
                        xs2AStandard.getAspsp().getName(),
                        xs2AStandard.getAspsp().getId(),
                        xs2AStandard.getAspsp().getAspspGroup().getName(),
                        xs2AStandard.getAspsp().getAspspGroup().getId(),
                        xs2AStandard.getAspsp().getConfig().getStandard().getName(),
                        xs2AStandard.getAspsp().getConfig().getStandard().getVersion(),
                        xs2AStandard.getAis(),
                        xs2AStandard.getCs(),
                        xs2AStandard.getPis(),
                        xs2AStandard.getPiis(),
                        xs2AStandard.getAspsp().getConfig().getImplementerOptions() != null ? xs2AStandard.getAspsp().getConfig().getImplementerOptions().size() : 0);
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
