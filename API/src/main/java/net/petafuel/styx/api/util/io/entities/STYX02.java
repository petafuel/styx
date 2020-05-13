package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOInputContainer;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Base64;

/**
 * Get PSU-ID within the JWT Payload sub field within from the Authorisation Header
 * This is currently special for the <b>Spardabank</b>
 */
public class STYX02 implements ApplicableImplementerOption {
    private static final String IO = "STYX02";

    @Override
    public IOInputContainer apply(IOInputContainer ioInput) throws ImplementerOptionException {
        if (!ioInput.getIoParser().getOption("IO6", IOParser.Option.REQUIRED).getAsBoolean() || !ioInput.getIoParser().getOption(IO, IOParser.Option.REQUIRED).getAsBoolean()) {
            //do not apply if not required
            return ioInput;
        }

        //check if ais or pis
        //extract PSU-ID from authorisation header
        //set the extracted psu id to the current psu within the xs2a request
        //override the iocontainer request with the modified request

        XS2ARequest xs2ARequest = ioInput.getXs2ARequest();
        String psuId = extractPsuId(xs2ARequest.getHeaders().get(XS2AHeader.AUTHORIZATION));

        if (psuId == null) {
            throw new ImplementerOptionException("Unable to extract psu id from access token");
        }
        ioInput.getXs2ARequest().getPsu().setId(psuId);

        return ioInput;
    }

    private String extractPsuId(String authroisationHeader) throws ImplementerOptionException {
        String[] jwtParts = authroisationHeader != null ? authroisationHeader.split("\\.") : null;
        if (jwtParts == null || jwtParts.length < 2) {
            throw new ImplementerOptionException("Error parsing pre-auth access token to JWT");
        }
        String decoded = new String(Base64.getDecoder().decode(jwtParts[1]));
        try (Jsonb jsonb = JsonbBuilder.create()) {
            JsonObject jwtPayload = jsonb.fromJson(decoded, JsonObject.class);
            return jwtPayload.getString("sub", null);
        } catch (Exception e) {
            throw new ImplementerOptionException("Error extracting sub field from JWT Access Token for pre-step authentication", e);
        }
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
