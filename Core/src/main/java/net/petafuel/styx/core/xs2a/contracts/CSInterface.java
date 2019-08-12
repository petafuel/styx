package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.security.SignatureException;
import java.util.List;
import java.util.UUID;

public interface CSInterface {

    Consent createConsent(PSU psu, List<Account> balances, List<Account> transactions, UUID consentId) throws SignatureException, BankRequestFailedException;

    Consent getConsent();

    Consent getStatus();

    Consent deleteConsent();

}
