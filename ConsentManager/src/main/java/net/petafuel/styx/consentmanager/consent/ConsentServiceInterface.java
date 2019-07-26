package net.petafuel.styx.consentmanager.consent;

import net.petafuel.styx.utils.http.XS2ARequest;
import net.petafuel.styx.utils.http.XS2AResponse;

public interface ConsentServiceInterface
{
    XS2AResponse createConsent(XS2ARequest xs2aRequest);

    XS2AResponse getConsent(XS2ARequest xs2aRequest);

    XS2AResponse getStatus(XS2ARequest xs2aRequest);

    XS2AResponse deleteConsent(XS2ARequest xs2aRequest);
}
