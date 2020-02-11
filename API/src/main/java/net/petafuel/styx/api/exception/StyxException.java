package net.petafuel.styx.api.exception;

import javax.ws.rs.WebApplicationException;

public class StyxException extends WebApplicationException {
    private final ErrorEntity errorEntity;
    private final Throwable attachedThrowable;

    public StyxException(ErrorEntity errorEntity) {
        super(errorEntity.getMessage(), errorEntity.getCode().getStatusCode());
        this.errorEntity = errorEntity;
        attachedThrowable = null;
    }

    public StyxException(ErrorEntity errorEntity, Throwable attachedThrowable) {
        super(errorEntity.getMessage(), errorEntity.getCode().getStatusCode());
        this.errorEntity = errorEntity;
        this.attachedThrowable = attachedThrowable;
    }

    public ErrorEntity getErrorEntity() {
        return errorEntity;
    }

    public Throwable getAttachedThrowable() {
        return attachedThrowable;
    }
}
