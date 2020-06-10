package net.petafuel.styx.spi.tokentypemapper;

import net.petafuel.styx.spi.AbstractServiceProvider;
import net.petafuel.styx.spi.paymentstatushook.impl.PaymentStatusHookImpl;
import net.petafuel.styx.spi.tokentypemapper.spi.TokenTypeMapperSPI;

public class TokenTypeMapperService extends AbstractServiceProvider<TokenTypeMapperSPI> {
    private static final String DEFAULT_PROVIDER = PaymentStatusHookImpl.class.getName();

    @Override
    protected String getDefaultProviderClassName() {
        return DEFAULT_PROVIDER;
    }

    @Override
    protected Class<TokenTypeMapperSPI> getSPIClass() {
        return TokenTypeMapperSPI.class;
    }
}