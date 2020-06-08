package net.petafuel.styx.testplugin.paymentstatushook;

import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.spi.paymentstatushook.api.HookStatus;
import net.petafuel.styx.spi.paymentstatushook.spi.PaymentStatusHookSPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InstantReplenishmentHook implements PaymentStatusHookSPI {
    private static final Logger LOG = LogManager.getLogger(InstantReplenishmentHook.class);

    @Override
    public void initialize(PaymentService paymentService, PaymentProduct paymentProduct, String paymentId, String bic) {
        LOG.info("pF-InstantReplenishmentHook initialized");
    }

    @Override
    public HookStatus onStatusUpdate(PaymentStatus paymentStatus) {
        LOG.info("pF-InstantReplenishmentHook onStatusUpdatetransactionStatus={}", paymentStatus.getTransactionStatus());
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
    public void onSuccess() {
        LOG.info("pF-InstantReplenishmentHook check if payment was made with instant-replenishment token");
        LOG.info("pF-InstantReplenishmentHook get full payment object from xs2a interface");
        LOG.info("pF-InstantReplenishmentHook parsing approval code");
        LOG.info("pF-InstantReplenishmentHook calling pfRest as success with approvalcode attached");
    }

    @Override
    public void onFailure() {
        LOG.info("pF-InstantReplenishmentHook calling pfRest as failure");
    }
}
