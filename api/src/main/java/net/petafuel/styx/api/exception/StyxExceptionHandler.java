package net.petafuel.styx.api.exception;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class StyxExceptionHandler implements ExceptionMapper<StyxException> {
    private static final Logger LOG = LogManager.getLogger(StyxExceptionHandler.class);

    @Override
    public Response toResponse(StyxException e) {
        Level logLevel;
        if (e.getResponseEntity().getOrigin().equals(ResponseOrigin.CLIENT)) {
            logLevel = Level.WARN;
        } else {
            logLevel = Level.ERROR;
        }
        String code = e.getResponseEntity().getCode().name();
        LOG.log(logLevel, "StyxException happened: category={}, origin={}, code={}, httpStatus={}, message={}, throwableMessage={}",
                e.getResponseEntity().getCategory(),
                e.getResponseEntity().getOrigin(),
                code,
                e.getResponseEntity().getCode().getStatusCode(),
                e.getResponseEntity().getMessage(),
                e.getAttachedThrowable() != null ? e.getAttachedThrowable().getMessage() : "");
        return Response.status(e.getResponseEntity().getCode().getStatusCode()).type(MediaType.APPLICATION_JSON).entity(e.getResponseEntity()).build();
    }
}
