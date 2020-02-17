package net.petafuel.styx.api.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
            message.append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append("\n");
        }
        ErrorEntity errorEntity = new ErrorEntity(message.toString(), Response.Status.BAD_REQUEST, ErrorCategory.STYX);
        return Response.status(errorEntity.getCode()).entity(errorEntity).build();
    }

}

