package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;

/**
 * This Interface defines the Classes that are used by XS2ARequestFactories in order to initialize Standard specific Requests
 */
public interface XS2ARequestClassProvider {
    //Consent Request Classes
    Class<? extends AISRequest> consentCreation();

    Class<? extends AISRequest> consentStatus();

    Class<? extends AISRequest> consentRetrieval();

    Class<? extends AISRequest> consentRemoval();

    //AIS Request Classes
    Class<? extends AISRequest> accountList();

    Class<? extends AISRequest> accountDetails();

    Class<? extends AISRequest> accountBalances();

    Class<? extends AISRequest> accountTransactionList();

    Class<? extends AISRequest> accountTransactionDetails();

    //PIS Request Classes
    Class<? extends PISRequest> paymentInitiation();

    Class<? extends PISRequest> paymentStatus();

    Class<? extends PISRequest> paymentRetrieval();

    Class<? extends PISRequest> paymentCancellation();

    Class<? extends PISRequest> paymentCancellationAuthorisation();

    //SCA Request Classes
    Class<? extends SCARequest> scaStart();

    Class<? extends SCARequest> scaUpdateAuthentication();

    Class<? extends SCARequest> scaUpdateIdentification();

    Class<? extends SCARequest> scaUpdateAuthenticationMethod();

    Class<? extends SCARequest> scaAuthoriseTransaction();

    Class<? extends SCARequest> scaStatus();

    Class<? extends SCARequest> scaRetrieval();

}
