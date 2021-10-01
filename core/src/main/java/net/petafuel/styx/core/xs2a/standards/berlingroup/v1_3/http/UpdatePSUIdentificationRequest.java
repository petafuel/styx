package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import java.util.Optional;

public class UpdatePSUIdentificationRequest extends SCARequest {
    // "/v1/{payment-service}/{payment.product}/{paymentId}/authorisations/{authorisationId}"
    public static final String PIS_UPDATE_PSU_DATA = "/v1/%s/%s/%s/authorisations/%s";
    // "/v1/consents/{consentId}/authorisations/{authorsationId}"
    public static final String CS_UPDATE_PSU_DATA = "/v1/consents/%s/authorisations/%s";

    public UpdatePSUIdentificationRequest(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId) {
        super(paymentService, paymentProduct, paymentId);
    }

    public UpdatePSUIdentificationRequest(String consentId) {
        super(consentId);
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }

    @Override
    public BasicService.RequestType getHttpMethod() {
        return BasicService.RequestType.PUT;
    }

    @Override
    public String getServicePath() {
        if (isPIS()) {
            return String.format(PIS_UPDATE_PSU_DATA,
                    getPaymentService().getValue(),
                    getPaymentProduct().getValue(),
                    getPaymentId(),
                    getAuthorisationId());

        } else {
            return String.format(CS_UPDATE_PSU_DATA, getConsentId(), getAuthorisationId());
        }
    }
}
