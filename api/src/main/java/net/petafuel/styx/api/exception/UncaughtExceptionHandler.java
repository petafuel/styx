package net.petafuel.styx.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UncaughtExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LogManager.getLogger(UncaughtExceptionHandler.class);

    @Override
    public Response toResponse(Throwable throwable) {
        ResponseEntity responseEntity = new ResponseEntity("Internal server error occurred ", ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX);
        LOG.error("Uncaught Throwable happened: category={}, code={}, httpStatus={}",
                responseEntity.getCategory(),
                responseEntity.getCode(),
                responseEntity.getCode().getStatusCode(),
                throwable);
        return Response.status(responseEntity.getCode().getStatusCode()).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
    }
}
