package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Optional;

public class DeleteConsentRequest extends XS2ARequest {

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

}
