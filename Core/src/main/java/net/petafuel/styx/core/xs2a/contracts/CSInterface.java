package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;

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

    SCA startAuthorisationProcess(XS2ARequest consentCreateAuthResourceRequest) throws BankRequestFailedException;

    void updatePSUData(XS2ARequest consentUpdatePSUDataRequest) throws BankRequestFailedException;

    SCA startAuthorisation(StartAuthorisationRequest request) throws BankRequestFailedException;
}
