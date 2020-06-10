package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.factory.XS2ARequestClassProvider;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountListRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadBalancesRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.AuthoriseTransactionRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.SelectAuthenticationMethodRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUAuthenticationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUIdentificationRequest;

public class BerlinGroupRequestProvider implements XS2ARequestClassProvider {
    @Override
    public Class<? extends AISRequest> consentCreation() {
        return CreateConsentRequest.class;
    }

    @Override
    public Class<? extends AISRequest> consentStatus() {
        return StatusConsentRequest.class;
    }

    @Override
    public Class<? extends AISRequest> consentRetrieval() {
        return GetConsentRequest.class;
    }

    @Override
    public Class<? extends AISRequest> consentRemoval() {
        return DeleteConsentRequest.class;
    }

    @Override
    public Class<? extends AISRequest> accountList() {
        return ReadAccountListRequest.class;
    }

    @Override
    public Class<? extends AISRequest> accountDetails() {
        return ReadAccountDetailsRequest.class;
    }

    @Override
    public Class<? extends AISRequest> accountBalances() {
        return ReadBalancesRequest.class;
    }

    @Override
    public Class<? extends AISRequest> accountTransactionList() {
        return ReadTransactionsRequest.class;
    }

    @Override
    public Class<? extends AISRequest> accountTransactionDetails() {
        return ReadTransactionDetailsRequest.class;
    }

    @Override
    public Class<? extends PISRequest> paymentInitiation() {
        return PaymentInitiationRequest.class;
    }

    @Override
    public Class<? extends PISRequest> paymentStatus() {
        return ReadPaymentStatusRequest.class;
    }

    @Override
    public Class<? extends PISRequest> paymentRetrieval() {
        return ReadPaymentRequest.class;
    }

    @Override
    public Class<? extends PISRequest> paymentCancellation() {
        return null;
    }

    @Override
    public Class<? extends PISRequest> paymentCancellationAuthorisation() {
        return null;
    }

    @Override
    public Class<? extends SCARequest> scaStart() {
        return StartAuthorisationRequest.class;
    }

    @Override
    public Class<? extends SCARequest> scaUpdateAuthentication() {
        return UpdatePSUAuthenticationRequest.class;
    }

    @Override
    public Class<? extends SCARequest> scaUpdateIdentification() {
        return UpdatePSUIdentificationRequest.class;
    }

    @Override
    public Class<? extends SCARequest> scaUpdateAuthenticationMethod() {
        return SelectAuthenticationMethodRequest.class;
    }

    @Override
    public Class<? extends SCARequest> scaAuthoriseTransaction() {
        return AuthoriseTransactionRequest.class;
    }

    @Override
    public Class<? extends SCARequest> scaStatus() {
        return GetSCAStatusRequest.class;
    }

    @Override
    public Class<? extends SCARequest> scaRetrieval() {
        return GetAuthorisationsRequest.class;
    }
}
