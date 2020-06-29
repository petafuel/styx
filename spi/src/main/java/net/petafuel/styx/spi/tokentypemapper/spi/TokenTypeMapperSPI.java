package net.petafuel.styx.spi.tokentypemapper.spi;

import net.petafuel.styx.spi.ServiceProviderInterface;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;

import java.util.List;

/**
 * Interface to define custom styx access token service types
 */
public interface TokenTypeMapperSPI extends ServiceProviderInterface {
    /**
     * This will be called when a new styx access token is created in order to validate whether this type of access token
     * is allowed to be created
     *
     * @return a list of allowed service types that will be added to the already existing allowed services
     */
    List<String> getAllowedServiceTypes();

    /**
     * this method should map the new custom service types to the fixed server types used by styx access control
     *
     * @param tokenTypeString this can be any old string
     * @return returns a concrete token type of ais, pis, piis or aispis used in access control
     */
    XS2ATokenType getMapping(String tokenTypeString);
}
