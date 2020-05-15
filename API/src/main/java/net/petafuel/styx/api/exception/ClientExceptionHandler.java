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
        ResponseEntity responseEntity = new ResponseEntity(throwable.getMessage(), ResponseConstant.fromStatusCode(throwable.getResponse().getStatus()), ResponseCategory.ERROR, ResponseOrigin.CLIENT);
        LOG.warn("ClientErrorException happened: category={}, code={}, httpStatus={}, message={}",
                responseEntity.getCategory(),
                responseEntity.getCode(),
                responseEntity.getCode().getStatusCode(),
                throwable.getMessage());
        return Response.status(responseEntity.getCode().getStatusCode()).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
    }
}
