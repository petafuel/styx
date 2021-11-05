package net.petafuel.styx.api.util.io;

import net.petafuel.styx.api.ioprocessing.entities.ImplementerOptionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Base64;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpardaPreauthParseTest {
    @Test
    void testJWTParsing() throws ImplementerOptionException {
        //Prepare initialized objects
        String authroisationHeader = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJTVFlYVGVzdDtQU0QyO1hTMkE7c29tZWlkIiwibmFtZSI6Ikx1bGFkb3IiLCJpYXQiOjE1MTYyMzkwMjJ9.opDGt6XzgWE7Hrwy4TBWZlhDWVYhhYFOBv-1wFu8cAQ";
        String[] jwtParts = authroisationHeader.split("\\.");
        if (jwtParts.length < 2) {
            throw new ImplementerOptionException("Error parsing pre-auth access token to JWT");
        }
        String decoded = new String(Base64.getDecoder().decode(jwtParts[1]));
        try (Jsonb jsonb = JsonbBuilder.create()) {
            javax.json.JsonObject jwtPayload = jsonb.fromJson(decoded, JsonObject.class);

            Assertions.assertNotNull(jwtPayload.getString("sub", null));
        } catch (Exception e) {
            throw new ImplementerOptionException("Error extracting sub field from JWT Access Token for pre-step authentication", e);
        }

    }

}
