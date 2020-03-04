package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

/**
 * Consent Service Interface
 * <p>
 *     <br>Handels communication between Styx and the consent related ASPSP interface functions
 * </p>
 */
public interface CSInterface {

    /**
     *
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent createConsent(XS2ARequest xs2ARequest) throws BankRequestFailedException;

    /**
     *
     * @param xs2ARequest
     * @return
     * @throws BankRequestFailedException
     */
    Consent getConsent(XS2ARequest xs2ARequest) throws BankRequestFailedException;

    /**
     *
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

    void updatePSUData(XS2ARequest consentUpdatePSUDataRequest) throws BankRequestFailedException;

    SCA startAuthorisation(XS2ARequest request) throws BankRequestFailedException;

    List<String> getAuthorisationRequest(XS2ARequest request) throws BankRequestFailedException;

    String getSCAStatus(XS2ARequest request) throws BankRequestFailedException;
}
