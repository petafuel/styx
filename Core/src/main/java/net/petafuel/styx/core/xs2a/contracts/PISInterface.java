package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

/**
 * This interface exposes payment related functionality
 *
 * @documented https://confluence.petafuel.intern/display/TOOL/Styx+Payment+Initiation+Interface
 */
public interface PISInterface extends SCAInterface {
    /**
     * Initiates a xs2a based payment within the ASPSP system
     *
     * @param xs2ARequest this should be a request that holds a payment structure, payment-service and payment-product
     * @return Returns data that contains further information on how to proceed with the payment
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    InitiatedPayment initiatePayment(XS2APaymentRequest xs2ARequest) throws BankRequestFailedException;

    /**
     * This will retrieve the payment status on the current payment
     *
     * @param request the request object must at least contain the payment-service, payment-product and payment id
     * @return returns a status constant how far the payment is processed on the aspsp side
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     * @see PaymentStatus
     */
    PaymentStatus getPaymentStatus(XS2APaymentRequest request) throws BankRequestFailedException;

    /**
     * GetPayment will retrieve the actual initialised payment from the ASPSP, either as json or pain.001
     * XML based payment structures will be reformed to fit the json structure
     * The payment might be altered by the aspsp, e.g. restructured remittance informations or changed the execution
     * date to fit the bank internal schedules.
     *
     * @param request the request object must at least contain the payment-service, payment-product and payment id
     * @return returns a previously initiated payment
     * @throws BankRequestFailedException in case anything request related(HTTP, De-/Serialisation) happens
     */
    InitializablePayment getPayment(XS2APaymentRequest request) throws BankRequestFailedException;
}
