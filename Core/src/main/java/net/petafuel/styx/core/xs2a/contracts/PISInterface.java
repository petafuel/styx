package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;

import java.util.List;

public interface PISInterface {

    InitiatedPayment initiatePayment(XS2APaymentInitiationRequest xs2ARequest) throws BankRequestFailedException;

    PaymentStatus getPaymentStatus(XS2ARequest request) throws BankRequestFailedException;

    InitializablePayment getPayment(XS2ARequest request) throws BankRequestFailedException;

    SCA startAuthorisation(XS2ARequest request) throws BankRequestFailedException;

    List<String> getAuthorisationRequest(GetAuthorisationRequest request) throws BankRequestFailedException;

    String getSCAStatus(GetSCAStatusRequest request) throws BankRequestFailedException;
}
