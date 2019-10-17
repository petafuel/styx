package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;

import java.security.SignatureException;

public interface PISInterface {

    InitiatedPayment initiatePayment(XS2ARequest xs2ARequest) throws SignatureException, BankRequestFailedException;

    PaymentStatus getPaymentStatus(XS2AGetRequest request) throws BankRequestFailedException;

}
