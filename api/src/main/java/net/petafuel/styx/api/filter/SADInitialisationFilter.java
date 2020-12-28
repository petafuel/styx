package net.petafuel.styx.api.filter;

import net.petafuel.styx.api.WebServer;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.api.rest.StyxFilterPriorites;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@Priority(StyxFilterPriorites.ASPSP_DIRECTORY_INIT)
public class SADInitialisationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(SADInitialisationFilter.class);

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        if (containerRequestContext.getProperty(BICFilter.class.getName()) == null) {
            LOG.info("XS2AStandard was not initialized as there was no BICFilter in place for the requested Resource");
            return;
        }
        String bic = (String) containerRequestContext.getProperty(BICFilter.class.getName());
        XS2AStandard xs2AStandard;
        try {
            xs2AStandard = new SAD().getBankByBIC(bic, WebServer.isSandbox());
            if (Boolean.FALSE.equals(xs2AStandard.getAspsp().isActive())) {
                throw new StyxException(new ResponseEntity("ASPSP with bic=" + xs2AStandard.getAspsp().getBic() + " is inactive", ResponseConstant.SAD_ASPSP_INACTIVE, ResponseCategory.ERROR, ResponseOrigin.STYX));
            }
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
        } catch (BankNotFoundException bicNotFound) {
            throw new StyxException(new ResponseEntity(bicNotFound.getMessage(), ResponseConstant.SAD_ASPSP_NOT_FOUND, ResponseCategory.ERROR, ResponseOrigin.STYX));
        } catch (BankLookupFailedException internalSADError) {
            throw new StyxException(new ResponseEntity("SAD was unable to initialize required Services", ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX), internalSADError);
        }
        containerRequestContext.setProperty(XS2AStandard.class.getName(), xs2AStandard);
    }
}
