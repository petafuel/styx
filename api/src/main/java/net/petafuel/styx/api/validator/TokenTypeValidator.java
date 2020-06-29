package net.petafuel.styx.api.validator;

import net.petafuel.styx.spi.tokentypemapper.TokenTypeMapperService;
import net.petafuel.styx.spi.tokentypemapper.spi.TokenTypeMapperSPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class TokenTypeValidator implements ConstraintValidator<ValidateTokenType, String> {
    private static final Logger LOG = LogManager.getLogger(TokenTypeValidator.class);
    private List<String> valueList;

    @Override
    public void initialize(ValidateTokenType constraintAnnotation) {
        valueList = new ArrayList<>();
        List<TokenTypeMapperSPI> tokenTypeMapperProviders = new TokenTypeMapperService().providers();
        if (tokenTypeMapperProviders.isEmpty()) {
            LOG.warn("There were no TokenTypeMapperServices found as SPI implementations");
        }
        tokenTypeMapperProviders.forEach(provider -> valueList.addAll(provider.getAllowedServiceTypes()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            //nothing to validate, use @NotNull
            return true;
        }
        return valueList.contains(value);
    }
}
