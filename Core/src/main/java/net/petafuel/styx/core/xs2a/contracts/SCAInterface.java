package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

/**
 * These methods are used to go through an SCA
 *
 * @documented https://confluence.petafuel.intern/display/TOOL/Styx+SCA+Interface
 */
public interface SCAInterface {
    /**
     * Start the SCA for a PSU if the bank did not start the sca implicitly
     *
     * @param xs2AAuthorisationRequest the request might contain psu informations, psu authentication data or a authenticationMethodId
     * @return returns an SCA Container
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    SCA startAuthorisation(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    /**
     * In case there are multiple Authorisation Resources for one Payment or Consent Resource we can retrieve the list
     * of authorisation ids
     *
     * @param xs2AAuthorisationRequest this should contain a payment id or consent id
     * @return a list of authorisation ids as Strings
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    List<String> getAuthorisations(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    /**
     * This retrieves the current status of an SCA
     *
     * @param xs2AAuthorisationRequest the request should contain the authorisation id
     * @return returns the SCA status constant
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    SCA.Status getSCAStatus(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    /**
     * This will update PSU information for the aspsp if they are missing for the current transaction
     *
     * @param xs2AAuthorisationRequest should contain a filled psu object
     * @return returns an SCA Container with further information
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    SCA updatePSUIdentification(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    /**
     * This updates the PSU authentication, basically this is a login call for a PSU
     *
     * @param xs2AAuthorisationRequest this should have a filled PSUData object
     * @return returns an SCA Container with further information
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    SCA updatePSUAuthentication(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    /**
     * Updates the chosen SCAMethod for the PSU on the ASPSP interface
     *
     * @param xs2AAuthorisationRequest should contain the authorisationMethodId
     * @return returns an SCA Container with further information
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    SCA selectAuthenticationMethod(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    /**
     * This will finalize a Transaction by completing the SCA with a TAN
     *
     * @param xs2AAuthorisationRequest should contain the scaAuthenticationData which is a TAN/Code
     * @return returns an SCA Container. The SCA Status should be finalized if everything went correctly
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    SCA authoriseTransaction(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException;
}
