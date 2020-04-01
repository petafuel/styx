package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentSerializer;
import net.petafuel.styx.core.xs2a.utils.Config;

import java.util.Optional;
import java.util.UUID;

public class CreateConsentRequest extends XS2ARequest {
    Consent consent;

    public CreateConsentRequest(Consent consent) {
        this.consent = consent;
        if (consent.getxRequestId() == null) {
            UUID uuid = UUID.randomUUID();
            this.setXrequestId(uuid.toString());
            consent.setxRequestId(uuid);
        } else {
            this.setXrequestId(consent.getxRequestId().toString());
        }
        this.setPsu(consent.getPsu());
        this.setTppRedirectUri(Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.getXrequestId());
        this.setTppNokRedirectUri(Config.getInstance().getProperties().getProperty("styx.redirect.baseurl") + this.getXrequestId());
    }

    @Override
    public Optional<String> getRawBody() {
        Gson gson = new GsonBuilder().registerTypeAdapter(CreateConsentRequest.class, new ConsentSerializer()).create();
        return Optional.of(gson.toJson(this));
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }
}
