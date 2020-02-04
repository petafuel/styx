package net.petafuel.styx.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class StyxExceptionHandler implements ExceptionMapper<StyxException> {

    private static final Logger LOG = LogManager.getLogger(StyxExceptionHandler.class);

    @Override
    public Response toResponse(StyxException e) {
        LOG.error("StyxException happened: category={}, code={}, httpStatus={}, message={}, trace={}",
                e.getErrorEntity().getCategory(),
                e.getErrorEntity().getCode(),
                e.getErrorEntity().getCode().getStatusCode(),
                e.getErrorEntity().getMessage(),
                e.getStackTrace());
        return Response.status(e.getErrorEntity().getCode().getStatusCode()).entity(e.getErrorEntity()).build();
    }
}
