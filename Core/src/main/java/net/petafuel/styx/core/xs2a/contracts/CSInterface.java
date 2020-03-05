package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

/**
 * Consent Service Interface
 * <p>
 * <br>Handels communication between Styx and the consent related ASPSP interface functions
 * </p>
 */
public interface CSInterface extends SCAInterface {

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent createConsent(XS2ARequest xs2ARequest) throws BankRequestFailedException;

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent getConsent(XS2ARequest xs2ARequest) throws BankRequestFailedException;

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent.State getStatus(XS2ARequest xs2ARequest) throws BankRequestFailedException;

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent deleteConsent(XS2ARequest xs2ARequest) throws BankRequestFailedException;
}
