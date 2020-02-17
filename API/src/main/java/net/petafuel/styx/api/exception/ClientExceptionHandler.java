package net.petafuel.styx.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * handle 4xx http error cases
 */
public class ClientExceptionHandler implements ExceptionMapper<ClientErrorException> {
    private static final Logger LOG = LogManager.getLogger(ClientExceptionHandler.class);

    @Override
    public Response toResponse(ClientErrorException throwable) {
        ErrorEntity errorEntity = new ErrorEntity(throwable.getMessage(), Response.Status.fromStatusCode(throwable.getResponse().getStatus()), ErrorCategory.STYX);
        LOG.warn("ClientErrorException happened: category={}, code={}, httpStatus={}, message={}, trace={}",
                errorEntity.getCategory(),
                errorEntity.getCode(),
                errorEntity.getCode().getStatusCode(),
                throwable.getMessage(),
                throwable.getStackTrace());
        return Response.status(errorEntity.getCode().getStatusCode()).type(MediaType.APPLICATION_JSON).entity(errorEntity).build();
    }
}
