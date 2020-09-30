package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.ConsentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

/**
 * Consent Service Interface
 * <p>
 * Handles communication between Styx and the consent related ASPSP interface functions.
 */
public interface CSInterface extends SCAInterface {

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent createConsent(AISRequest xs2ARequest) throws BankRequestFailedException;

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent getConsent(AISRequest xs2ARequest) throws BankRequestFailedException;

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    ConsentStatus getStatus(AISRequest xs2ARequest) throws BankRequestFailedException;

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent deleteConsent(AISRequest xs2ARequest) throws BankRequestFailedException;
}
