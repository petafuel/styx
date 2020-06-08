package net.petafuel.styx.api.v1.authentication.control;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.persistence.layers.PersistentAccessToken;

import javax.json.Json;
import javax.json.JsonObject;
import java.security.NoSuchAlgorithmException;

public class AuthenticationHandler {
    private AuthenticationHandler() {
    }

    public static JsonObject createAccessToken(String masterTokenHash, String service, Integer expiresIn) {
        String plainToken = TokenGenerator.generateRandomBytes();

        try {
            PersistentAccessToken.create(masterTokenHash, TokenGenerator.hashSHA256(plainToken), service, expiresIn);
        } catch (NoSuchAlgorithmException e) {
            ResponseEntity responseEntity = new ResponseEntity(e.getMessage(), ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX);
            throw new StyxException(responseEntity);
        }

        return Json.createObjectBuilder().add("token", plainToken).build();
    }
}
