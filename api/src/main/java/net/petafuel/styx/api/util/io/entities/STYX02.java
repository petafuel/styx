package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Base64;

/**
 * Get PSU-ID within the JWT Payload sub field within from the Authorisation Header
 * This is currently special for the <b>Spardabank</b>
 */
public class STYX02 extends ApplicableImplementerOption {
    private static final String IO = "STYX02";

    public STYX02(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public void apply(XS2AFactoryInput ioInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        if (Boolean.FALSE.equals(ioParser.getOption("IO6", IOParser.Option.REQUIRED)) || Boolean.FALSE.equals(ioParser.getOption(IO, IOParser.Option.REQUIRED))) {
            //do not apply if not required
            return;
        }

        //check if ais or pis
        //extract PSU-ID from authorisation header
        //set the extracted psu id to the current psu within the xs2a request
        //override the iocontainer request with the modified request

        String psuId = extractPsuId(xs2ARequest.getHeaders().get(XS2AHeader.AUTHORIZATION));

        if (psuId == null) {
            throw new ImplementerOptionException("Unable to extract psu id from access token");
        }
        if (ioInput.getPsu() != null) {
            xs2ARequest.setPsu(ioInput.getPsu());
        } else {
            xs2ARequest.setPsu(new PSU());
        }
        xs2ARequest.getPsu().setId(psuId);
    }

    public String extractPsuId(String authroisationHeader) throws ImplementerOptionException {
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
