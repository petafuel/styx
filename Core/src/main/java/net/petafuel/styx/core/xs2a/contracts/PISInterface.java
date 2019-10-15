package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import java.security.SignatureException;

public interface PISInterface {

    InitiatedPayment initiatePaymentPain001(XS2ARequest xs2ARequest) throws SignatureException, BankRequestFailedException;

}
