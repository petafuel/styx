package net.petafuel.styx.api.exception;

import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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

        LOG.error("Bank request failed with responseCode={}. See following lines", responseEntity.getCode().getStatusCode());
        try (Jsonb jsonb = JsonbBuilder.create()) {
            TPPMessagesWrapper tppMessagesWrapper = jsonb.fromJson(throwable.getMessage(), TPPMessagesWrapper.class);
            tppMessagesWrapper.getTppMessages().forEach(tppMsg -> LOG.error("TPPMessage code={}, text={}, category={}, path={}", tppMsg.getCode(), tppMsg.getText(), tppMsg.getCategory(), tppMsg.getPath()));
        } catch (Exception e) {
            LOG.error("Unable to deserialize TPPMessages rawMessage={}", throwable.getMessage());
        }
        return Response.status(responseEntity.getCode().getStatusCode()).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
    }
}
