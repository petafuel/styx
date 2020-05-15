package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.utils.Config;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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
        try (Jsonb jsonb = JsonbBuilder.create()) {
            return Optional.of(jsonb.toJson(consent));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }
}
