package net.petafuel.styx.spi.tokentypemapper.spi;

import net.petafuel.styx.spi.ServiceProviderInterface;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;

import java.util.List;

public interface TokenTypeMapperSPI extends ServiceProviderInterface {
    List<String> getAllowedServiceTypes();

    XS2ATokenType getMapping(String tokenTypeString);
}
