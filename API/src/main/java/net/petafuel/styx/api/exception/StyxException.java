package net.petafuel.styx.api.exception;

import javax.ws.rs.WebApplicationException;

public class StyxException extends WebApplicationException {
    private final ResponseEntity responseEntity;
    private final Throwable attachedThrowable;

    public StyxException(ResponseEntity responseEntity) {
        super(responseEntity.getMessage(), responseEntity.getCode().getStatusCode());
        this.responseEntity = responseEntity;
        attachedThrowable = null;
    }

    public StyxException(ResponseEntity responseEntity, Throwable attachedThrowable) {
        super(responseEntity.getMessage(), responseEntity.getCode().getStatusCode());
        this.responseEntity = responseEntity;
        this.attachedThrowable = attachedThrowable;
    }

    public ResponseEntity getResponseEntity() {
        return responseEntity;
    }

    public Throwable getAttachedThrowable() {
        return attachedThrowable;
    }
}
