package net.petafuel.styx.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Handle constraint validations
 */
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger LOG = LogManager.getLogger(ConstraintViolationExceptionHandler.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
            message.append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append("\n");
        }
        ResponseEntity responseEntity = new ResponseEntity(message.toString(), ResponseConstant.BAD_REQUEST, ResponseCategory.ERROR, ResponseOrigin.CLIENT);
        LOG.warn("ConstraintViolationException happened: category={}, code={}, httpStatus={}, message={}, trace={}",
                responseEntity.getCategory(),
                responseEntity.getCode(),
                responseEntity.getCode().getStatusCode(),
                exception.getMessage(),
                exception.getStackTrace());
        return Response.status(responseEntity.getCode()).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
    }

}

