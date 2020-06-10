package net.petafuel.styx.spi.paymentstatushook.impl;

import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.spi.paymentstatushook.api.HookStatus;
import net.petafuel.styx.spi.paymentstatushook.spi.PaymentStatusHookSPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentStatusHookImpl implements PaymentStatusHookSPI {
    private static final Logger LOG = LogManager.getLogger(PaymentStatusHookImpl.class);

    @Override
    public void initialize(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, String bic) {
        LOG.debug("New payment passed ot the PaymentStatusHookImpl Service for paymentService={}, paymentProduct={}, paymentId={}, bic={}",
                paymentService,
                paymentProduct,
                paymentId,
                bic);
    }

    @Override
    public HookStatus onStatusUpdate(PaymentStatus paymentStatus) {
        LOG.debug("Received Status update from Keep Alive Task. transactionStatus={}", paymentStatus.getTransactionStatus());
        HookStatus status;
        switch (paymentStatus.getTransactionStatus()) {
            case ACCC:
            case ACCP:
            case ACSC:
            case ACSP:
            case ACTC:
            case ACWC:
            case ACWP:
            case ACFC:
                LOG.debug("transactionStatus={} payment is successful", paymentStatus.getTransactionStatus());
                status = HookStatus.SUCCESS;
                break;

            case RCVD:
            case PDNG:
            case PATC:
            case PART:
                LOG.debug("transactionStatus={} payment is still not processed by the aspsp", paymentStatus.getTransactionStatus());
                status = HookStatus.RETRY;
                break;

            case RJCT:
            case CANC:
                LOG.debug("transactionStatus={} payment was rejected by the ASPSP or cancelled by the PSU", paymentStatus.getTransactionStatus());
                status = HookStatus.FAILURE;
                break;
            default:
                LOG.debug("transactionStatus={} no case specified for this status, aborting", paymentStatus.getTransactionStatus());
                status = HookStatus.FAILURE;
        }
        return status;
    }

    @Override
    public void onSuccess(InitializablePayment payment) {
        Boolean paymentReceived = payment != null;
        LOG.debug("PaymentStatus was successful, processing success case, payment is present={}", paymentReceived);
    }

    @Override
    public void onFailure(InitializablePayment payment) {
        Boolean paymentReceived = payment != null;
        LOG.debug("Payment failed, processing failure case, payment is present={}", paymentReceived);
    }
}
