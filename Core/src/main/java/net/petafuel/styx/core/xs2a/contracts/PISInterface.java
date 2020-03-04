package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.XS2APaymentRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

public interface PISInterface {

    InitiatedPayment initiatePayment(XS2APaymentRequest xs2ARequest) throws BankRequestFailedException;

    PaymentStatus getPaymentStatus(XS2ARequest request) throws BankRequestFailedException;

    InitializablePayment getPayment(XS2APaymentRequest request) throws BankRequestFailedException;

    SCA startAuthorisation(XS2ARequest request) throws BankRequestFailedException;

    List<String> getAuthorisationRequest(XS2ARequest request) throws BankRequestFailedException;

    String getSCAStatus(XS2ARequest request) throws BankRequestFailedException;
}
