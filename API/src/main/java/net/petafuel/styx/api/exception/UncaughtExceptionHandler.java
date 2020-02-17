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
        ErrorEntity errorEntity = new ErrorEntity(throwable.getMessage(), Response.Status.INTERNAL_SERVER_ERROR, ErrorCategory.STYX);
        LOG.fatal("Uncaught Throwable happened: category={}, code={}, httpStatus={}, message={}, cause={}, trace={}",
                errorEntity.getCategory(),
                errorEntity.getCode(),
                errorEntity.getCode().getStatusCode(),
                throwable.getMessage(),
                throwable.getCause() != null ? throwable.getCause().getMessage() : "",
                throwable.getStackTrace());
        return Response.status(errorEntity.getCode().getStatusCode()).type(MediaType.APPLICATION_JSON).entity(errorEntity).build();
    }
}
