package net.petafuel.styx.testplugin.tokentypemapper;

import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import net.petafuel.styx.spi.tokentypemapper.spi.TokenTypeMapperSPI;

import java.util.Collections;
import java.util.List;

public class InstantReplenishmentTokenMapper implements TokenTypeMapperSPI {
    private static final String VIMPAY_INSTANT_REPLENISHMENT = "pis-vimpay-instant-replenishment";

    @Override
    public List<String> getAllowedServiceTypes() {
        return Collections.singletonList(VIMPAY_INSTANT_REPLENISHMENT);
    }

    @Override
    public XS2ATokenType getMapping(String tokenTypeString) {
        if (VIMPAY_INSTANT_REPLENISHMENT.equals(tokenTypeString)) {
            return XS2ATokenType.PIS;
        } else {
            return null;
        }
    }
}
