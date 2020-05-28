package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

/**
 * Consent Service Interface
 * <p>
 * <br>Handels communication between Styx and the consent related ASPSP interface functions
 * </p>
 *
 * @documented https://confluence.petafuel.intern/display/TOOL/Styx+Consent+Service+Interface
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
    Consent.State getStatus(AISRequest xs2ARequest) throws BankRequestFailedException;

    /**
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent deleteConsent(AISRequest xs2ARequest) throws BankRequestFailedException;
}
