package net.petafuel.styx.spi.tokentypemapper.impl;

import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import net.petafuel.styx.spi.tokentypemapper.spi.TokenTypeMapperSPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class TokenTypeMapperImpl implements TokenTypeMapperSPI {
    private static final Logger LOG = LogManager.getLogger(TokenTypeMapperImpl.class);

    @Override
    public List<String> getAllowedServiceTypes() {
        LOG.debug("getAllowedServiceTypes from TokenTypeMapperImpl default");
        return Arrays.asList("pis", "ais", "piis", "aispis");
    }

    @Override
    public XS2ATokenType getMapping(String tokenTypeString) {
        LOG.debug("getMapping from TokenTypeMapperImpl default");
        return XS2ATokenType.getByString(tokenTypeString);
    }
}
