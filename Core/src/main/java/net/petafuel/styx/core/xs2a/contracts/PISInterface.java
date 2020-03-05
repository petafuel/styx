package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

public interface PISInterface extends SCAInterface {

    InitiatedPayment initiatePayment(XS2APaymentRequest xs2ARequest) throws BankRequestFailedException;

    PaymentStatus getPaymentStatus(XS2ARequest request) throws BankRequestFailedException;

    InitializablePayment getPayment(XS2ARequest request) throws BankRequestFailedException;
}
