package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

public interface PISInterface {

    PaymentStatus getPaymentStatus(XS2AGetRequest request) throws BankRequestFailedException;

}
