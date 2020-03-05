package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import java.util.Optional;

public class UpdatePSUIdentificationRequest extends XS2AAuthorisationRequest {
    // "/v1/{payment-service}/{payment.product}/{paymentId}/authorisations/{authorisationId}"
    public static final String PIS_UPDATE_PSU_DATA = "/v1/%s/%s/%s/authorisations/%s";
    // "/v1/consents/{consentId}/authorisations/{authorsationId}"
    public static final String CS_UPDATE_PSU_DATA = "/v1/consents/%s/authorisations/%s";
    private String authorisationId;

    public UpdatePSUIdentificationRequest(PSU psu, PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, String authorisationId) {
        super(paymentService, paymentProduct, paymentId);
        this.authorisationId = authorisationId;
        setPsu(psu);
    }

    public UpdatePSUIdentificationRequest(PSU psu, String consentId, String authorisationId) {
        super(consentId);
        this.authorisationId = authorisationId;
        setPsu(psu);
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

    @Override
    public String getServiceURL() {
        if (isPIS()) {
            return String.format(PIS_UPDATE_PSU_DATA,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId(),
                    authorisationId);

        } else {
            return String.format(CS_UPDATE_PSU_DATA, getConsentId(), authorisationId);
        }
    }
}
