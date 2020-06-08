package net.petafuel.styx.spi.paymentstatushook.spi;


import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.spi.ServiceProviderInterface;
import net.petafuel.styx.spi.paymentstatushook.api.HookStatus;

/**
 * This interface describes methods that are going to be called by the PaymentStatusPollTask within keepalive
 */
public interface PaymentStatusHookSPI extends ServiceProviderInterface {
    /**
     * This is called once the payment status poll task is initialized
     *
     * @param paymentService payment-service for the target ASPSP
     * @param paymentProduct payment-product for the target ASPSP
     * @param paymentId      payment-id for the target ASPSP
     * @param bic            bic so SAD can be used
     */
    void initialize(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, String bic);

    /**
     * Called on each task iteration / after each payment status call towards the ASPSP
     *
     * @param paymentStatus the current task will pass this object which was received from the ASPSP
     * @return the return decides whether the task will continue with another iteration or aborts with success/failure
     */
    HookStatus onStatusUpdate(PaymentStatus paymentStatus);

    /**
     * Called if the HookStatus was returned as SUCCESS by onStatusUpdate
     *
     * @param payment the poll task will try to get the payment and pass it to the spi implementation - payment might be null
     */
    void onSuccess(InitializablePayment payment);

    /**
     * Called if the HookStatus was returned as FAILURE by onStatusUpdate
     *
     * @param payment the poll task will try to get the payment and pass it to the spi implementation - payment might be null
     */
    void onFailure(InitializablePayment payment);
}
