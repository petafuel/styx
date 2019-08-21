package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.security.SignatureException;

public interface CSInterface {

    Consent createConsent(XS2ARequest xs2ARequest) throws SignatureException, BankRequestFailedException;

    Consent getConsent(XS2ARequest xs2ARequest) throws BankRequestFailedException;

    Consent.State getStatus(XS2ARequest xs2ARequest) throws BankRequestFailedException;

    Consent deleteConsent(XS2ARequest xs2ARequest) throws BankRequestFailedException;

}
