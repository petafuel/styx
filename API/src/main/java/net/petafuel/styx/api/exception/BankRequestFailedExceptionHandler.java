package net.petafuel.styx.api.exception;

import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BankRequestFailedExceptionHandler implements ExceptionMapper<BankRequestFailedException> {
    private static final Logger LOG = LogManager.getLogger(BankRequestFailedExceptionHandler.class);

    @Override
    public Response toResponse(BankRequestFailedException throwable) {
        ResponseEntity responseEntity = new ResponseEntity(throwable.getMessage(), ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.ASPSP);

        if (throwable.getHttpStatusCode() != null && ResponseConstant.fromStatusCode(throwable.getHttpStatusCode()) != null) {
            responseEntity.setCode(ResponseConstant.fromStatusCode(throwable.getHttpStatusCode()));
        }
        LOG.error("Bank request failed responseCode={} message={}", responseEntity.getCode().getStatusCode(), responseEntity.getMessage());
        return Response.status(responseEntity.getCode().getStatusCode()).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
    }
}
