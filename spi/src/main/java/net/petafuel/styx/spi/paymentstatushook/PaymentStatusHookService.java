package net.petafuel.styx.spi.paymentstatushook;

import net.petafuel.styx.spi.AbstractServiceProvider;
import net.petafuel.styx.spi.paymentstatushook.impl.PaymentStatusHookImpl;
import net.petafuel.styx.spi.paymentstatushook.spi.PaymentStatusHookSPI;

public class PaymentStatusHookService extends AbstractServiceProvider<PaymentStatusHookSPI> {
    private static final String DEFAULT_PROVIDER = PaymentStatusHookImpl.class.getName();

    @Override
    protected String getDefaultProviderClassName() {
        return DEFAULT_PROVIDER;
    }

    @Override
    protected Class<PaymentStatusHookSPI> getSPIClass() {
        return PaymentStatusHookSPI.class;
    }
}
