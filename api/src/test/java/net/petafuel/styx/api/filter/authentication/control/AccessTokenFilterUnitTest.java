package net.petafuel.styx.api.filter.authentication.control;

import net.petafuel.styx.api.exception.StyxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccessTokenFilterUnitTest {

    @Test
    void testCheckToken() {
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        Assertions.assertThrows(StyxException.class, () -> accessTokenFilter.checkToken("invalid"));
    }
}