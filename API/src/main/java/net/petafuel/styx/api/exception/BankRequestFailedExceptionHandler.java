package net.petafuel.styx.api.exception;

import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BankRequestFailedExceptionHandler implements ExceptionMapper<BankRequestFailedException> {
    private static final Logger LOG = LogManager.getLogger(BankRequestFailedExceptionHandler.class);

    @Override
    public Response toResponse(BankRequestFailedException throwable) {
        ErrorEntity errorEntity = new ErrorEntity(throwable.getMessage(), Response.Status.INTERNAL_SERVER_ERROR, ErrorCategory.ASPSP);

        if (throwable.getHttpStatusCode() != null && Response.Status.fromStatusCode(throwable.getHttpStatusCode()) != null) {
            errorEntity.setCode(Response.Status.fromStatusCode(throwable.getHttpStatusCode()));
        }
        LOG.error("Bank request failed responseCode={} message={}", errorEntity.getCode().getStatusCode(), errorEntity.getMessage());
        return Response.status(errorEntity.getCode().getStatusCode()).entity(errorEntity).build();
    }
}
