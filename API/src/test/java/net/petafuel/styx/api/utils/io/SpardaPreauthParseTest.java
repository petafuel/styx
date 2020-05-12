package net.petafuel.styx.api.utils.io;

import com.google.gson.JsonObject;
import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerAIS;
import net.petafuel.styx.api.util.io.entities.ImplementerOptionException;
import net.petafuel.styx.api.util.io.entities.STYX02;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.Config;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpardaPreauthParseTest {
    @Test
    public void testJWTParsing() throws ImplementerOptionException {
        //Prepare initialized objects
        BasicRequest basicRequest = new BasicRequest();
        basicRequest.getHeaders().put(XS2AHeader.AUTHORIZATION, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJTVFlYVGVzdDtQU0QyO1hTMkE7c29tZWlkIiwibmFtZSI6Ikx1bGFkb3IiLCJpYXQiOjE1MTYyMzkwMjJ9.opDGt6XzgWE7Hrwy4TBWZlhDWVYhhYFOBv-1wFu8cAQ");
        XS2AStandard xs2AStandard = new XS2AStandard();
        xs2AStandard.setAspsp(new Aspsp());
        xs2AStandard.getAspsp().setConfig(new Config());

        ImplementerOption IOSTYX02 = new ImplementerOption();
        IOSTYX02.setId("STYX02");
        JsonObject jsonio02 = new JsonObject();
        jsonio02.addProperty("required", true);
        IOSTYX02.addOption(IOParser.Option.REQUIRED, jsonio02.get("required"));
        xs2AStandard.getAspsp().getConfig().getImplementerOptions().put("STYX02", IOSTYX02);

        ImplementerOption IO6 = new ImplementerOption();
        IO6.setId("IO6");
        JsonObject jsonio6 = new JsonObject();
        jsonio6.addProperty("required", true);
        IO6.addOption(IOParser.Option.REQUIRED, jsonio6.get("required"));
        xs2AStandard.getAspsp().getConfig().getImplementerOptions().put("IO6", IO6);

        IOInputContainerAIS ioInputContainerAIS = new IOInputContainerAIS(xs2AStandard, new PSU());
        ioInputContainerAIS.setXs2ARequest(basicRequest);

        STYX02 styx02 = new STYX02();
        IOInputContainerAIS ioInputContainer = (IOInputContainerAIS) styx02.apply(ioInputContainerAIS);
        Assertions.assertEquals("STYXTest;PSD2;XS2A;someid", ioInputContainer.getXs2ARequest().getPsu().getId());

    }

    private class BasicRequest extends XS2ARequest {
        @Override
        public Optional<String> getRawBody() {
            return Optional.empty();
        }
    }
}
