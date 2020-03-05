package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

public interface SCAInterface {
    SCA startAuthorisation(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    List<String> getAuthorisations(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    SCA.Status getSCAStatus(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    SCA updatePSUIdentification(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    SCA updatePSUAuthentication(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    SCA selectAuthenticationMethod(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException;

    SCA authoriseTransaction(XS2AAuthorisationRequest xs2AAuthorisationRequest) throws BankRequestFailedException;
}
