package net.petafuel.styx.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class StyxException extends WebApplicationException {
    private final ErrorEntity errorEntity;

    StyxException(Throwable throwable, Response.Status errorCode, ErrorCategory errorCategory) {
        super(throwable, errorCode.getStatusCode());
        errorEntity = new ErrorEntity(throwable.getMessage(), errorCode, errorCategory);
    }

    public StyxException(ErrorEntity errorEntity) {
        super(errorEntity.getMessage(), errorEntity.getCode().getStatusCode());
        this.errorEntity = errorEntity;
    }

    public ErrorEntity getErrorEntity() {
        return errorEntity;
    }
}
