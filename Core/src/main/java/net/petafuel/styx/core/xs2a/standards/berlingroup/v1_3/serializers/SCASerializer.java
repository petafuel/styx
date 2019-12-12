package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.xs2a.entities.AuthenticationObject;
import net.petafuel.styx.core.xs2a.entities.AuthenticationType;
import net.petafuel.styx.core.xs2a.entities.Challenge;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import net.petafuel.styx.core.xs2a.utils.DeserialisationHelper;

import java.lang.reflect.Type;
import java.util.List;

public class SCASerializer implements JsonSerializer<SCA>, JsonDeserializer<SCA> {
    @Override
    public SCA deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject authorisationResponse = jsonElement.getAsJsonObject();
        SCA sca = new SCA();
        JsonElement currentJsonElement;

        if ((currentJsonElement = authorisationResponse.get(XS2AJsonKeys.SCA_STATUS.value())) != null) {
            sca.setStatus(SCA.Status.valueOf(currentJsonElement.getAsString().toUpperCase()));
        }

        if ((currentJsonElement = authorisationResponse.get(XS2AJsonKeys.AUTHORISATION_ID.value())) != null) {
            sca.setAuthorisationId(currentJsonElement.getAsString());
        }

        //array of authentication objects
        if ((currentJsonElement = authorisationResponse.get(XS2AJsonKeys.SCA_METHODS.value())) != null) {
            JsonArray methods = currentJsonElement.getAsJsonArray();
            methods.forEach(scaMethodJsonElement -> sca.addScaMethod(parseAuthenticationObject(scaMethodJsonElement)));
        }

        //single authentication object
        if ((currentJsonElement = authorisationResponse.get(XS2AJsonKeys.CHOSEN_SCA_METHOD.value())) != null) {
            sca.setChosenSCAMethod(parseAuthenticationObject(currentJsonElement));
        }

        //Challenge object
        if ((currentJsonElement = authorisationResponse.get(XS2AJsonKeys.CHALLENGE.value())) != null) {
            sca.setChallengeData(parseChallenge(currentJsonElement));
        }

        if ((currentJsonElement = authorisationResponse.get(XS2AJsonKeys.LINKS.value())) != null && !authorisationResponse.get(XS2AJsonKeys.LINKS.value()).isJsonNull()) {
            DeserialisationHelper.parseSCALinksData(sca, currentJsonElement.getAsJsonObject());
        }

        if ((currentJsonElement = authorisationResponse.get(XS2AJsonKeys.PSU_MESSAGE.value())) != null) {
            sca.setPsuMessage(currentJsonElement.getAsString());
        }

        return sca;
    }

    @Override
    public JsonElement serialize(SCA sca, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }

    private AuthenticationObject parseAuthenticationObject(JsonElement chosenSCAMethod) {
        AuthenticationObject authenticationObject = new AuthenticationObject();
        JsonObject authenticationJsonObject = chosenSCAMethod.getAsJsonObject();

        authenticationObject.setAuthenticationType(AuthenticationType.valueOf(authenticationJsonObject.get(XS2AJsonKeys.AUTHENTICATION_TYPE.value()).getAsString()));

        JsonElement version;
        if ((version = authenticationJsonObject.get(XS2AJsonKeys.AUTHENTICATION_VERSION.value())) != null) {
            authenticationObject.setAuthenticationVersion(version.getAsString());
        }

        authenticationObject.setAuthenticationMethodId(authenticationJsonObject.get(XS2AJsonKeys.AUTHENTICATION_METHOD_ID.value()).getAsString());
        authenticationObject.setName(authenticationJsonObject.get(XS2AJsonKeys.AUTHENTICATION_NAME.value()).getAsString());

        JsonElement explanation;
        if ((explanation = authenticationJsonObject.get(XS2AJsonKeys.AUTHENTICATION_EXPLANATION.value())) != null) {
            authenticationObject.setExplanation(explanation.getAsString());
        }

        return authenticationObject;
    }

    private Challenge parseChallenge(JsonElement challengeJson) {
        Challenge challenge = new Challenge();
        JsonObject challengeJsonObject = challengeJson.getAsJsonObject();
        JsonElement currentJsonElement;

        if ((currentJsonElement = challengeJsonObject.get(XS2AJsonKeys.CHALLENGE_IMAGE.value())) != null) {
            challenge.setImage(currentJsonElement.getAsString());
        }

        if ((currentJsonElement = challengeJsonObject.get(XS2AJsonKeys.CHALLENGE_DATA.value())) != null) {
            challenge.setData((new Gson()).fromJson(currentJsonElement.getAsJsonArray().toString(), new TypeToken<List<String>>() {
            }.getType()));
        }

        if ((currentJsonElement = challengeJsonObject.get(XS2AJsonKeys.CHALLENGE_IMAGE_LINK.value())) != null) {
            challenge.setImageLink(currentJsonElement.getAsString());
        }

        if ((currentJsonElement = challengeJsonObject.get(XS2AJsonKeys.CHALLENGE_OTP_MAX_LENGTH.value())) != null) {
            challenge.setOtpMaxLength(currentJsonElement.getAsInt());
        }

        if ((currentJsonElement = challengeJsonObject.get(XS2AJsonKeys.CHALLENGE_OTP_FORMAT.value())) != null) {
            challenge.setOtpFormat(Challenge.OTP_FORMAT.valueOf(currentJsonElement.getAsString().toUpperCase()));
        }

        if ((currentJsonElement = challengeJsonObject.get(XS2AJsonKeys.CHALLENGE_ADDITIONAL_INFORMATION.value())) != null) {
            challenge.setAdditionalInformation(currentJsonElement.getAsString());
        }

        return challenge;
    }
}
