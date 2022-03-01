package net.petafuel.styx.api.filter.authentication.control;

import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.persistence.models.MasterToken;
import net.petafuel.styx.core.persistence.models.MasterTokenRestriction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class AccessTokenFilterUnitTest {

    @Test
    void testAccessTokenHasServiceTheMasterTokenDoesNotHaveAnymore() {
        AccessToken accessToken = new AccessToken();
        accessToken.setServiceType("ais");
        MasterToken masterToken = prepareMasterToken("pis", 2);
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        Assertions.assertThrows(StyxException.class, () -> accessTokenFilter.checkMaxUsages(masterToken, accessToken));
        try {
            accessTokenFilter.checkMaxUsages(masterToken, accessToken);
        } catch (StyxException exception) {
            ResponseEntity response = exception.getResponseEntity();
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getReasonPhrase(), response.getMessage());
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getStatusCode(), response.getCode().getStatusCode());
            Assertions.assertEquals(ResponseCategory.ERROR, response.getCategory());
            Assertions.assertEquals(ResponseOrigin.STYX, response.getOrigin());
        }
    }


    @Test
    void testCheckToken() {
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        Assertions.assertThrows(StyxException.class, () -> accessTokenFilter.checkToken("invalid"));
    }

    @Test
    void testCheckMaxUsagesReached() {
        AccessToken accessToken = new AccessToken();
        accessToken.setServiceType("ais");
        MasterToken masterToken = prepareMasterToken("ais", 2);
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        accessToken.setUsages(2);
        Assertions.assertThrows(StyxException.class, () -> accessTokenFilter.checkMaxUsages(masterToken, accessToken));
        try {
            accessTokenFilter.checkMaxUsages(masterToken, accessToken);
        } catch (StyxException exception) {
            ResponseEntity response = exception.getResponseEntity();
            Assertions.assertEquals(ResponseConstant.STYX_TOKEN_ACCESS_EXEEDED.getReasonPhrase(), response.getMessage());
            Assertions.assertEquals(ResponseConstant.STYX_TOKEN_ACCESS_EXEEDED.getStatusCode(), response.getCode().getStatusCode());
            Assertions.assertEquals(ResponseCategory.ERROR, response.getCategory());
            Assertions.assertEquals(ResponseOrigin.CLIENT, response.getOrigin());
        }
    }

    @Test
    void testCheckMaxUsagesNotReached() {
        AccessToken accessToken = new AccessToken();
        accessToken.setServiceType("pis");
        MasterToken masterToken = prepareMasterToken("pis", 5);
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        accessToken.setUsages(1);
        Assertions.assertDoesNotThrow(() -> accessTokenFilter.checkMaxUsages(masterToken, accessToken));
    }

    @Test
    void testMasterTokenRestrictionsNotSet() {
        MasterToken masterToken = new MasterToken();
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        Assertions.assertThrows(StyxException.class, () -> accessTokenFilter.checkRestrictions(masterToken, "pis"));
        try {
            accessTokenFilter.checkRestrictions(masterToken, "pis");
        } catch (StyxException exception) {
            ResponseEntity response = exception.getResponseEntity();
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getReasonPhrase(), response.getMessage());
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getStatusCode(), response.getCode().getStatusCode());
            Assertions.assertEquals(ResponseCategory.ERROR, response.getCategory());
            Assertions.assertEquals(ResponseOrigin.STYX, response.getOrigin());
        }
    }

    @Test
    void testCheckRestrictionsOnChangedMasterTokenRestrictions() {
        MasterToken masterToken = prepareMasterToken("pis", 2);
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        Assertions.assertThrows(StyxException.class, () -> accessTokenFilter.checkRestrictions(masterToken, "ais"));
        try {
            accessTokenFilter.checkRestrictions(masterToken, "ais");
        } catch (StyxException exception) {
            ResponseEntity response = exception.getResponseEntity();
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getReasonPhrase(), response.getMessage());
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getStatusCode(), response.getCode().getStatusCode());
            Assertions.assertEquals(ResponseCategory.ERROR, response.getCategory());
            Assertions.assertEquals(ResponseOrigin.STYX, response.getOrigin());
        }
    }

    @Test
    void testMasterTokenInvalidConfiguration() {
        MasterToken masterToken = prepareMasterToken("pis", null);
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        Assertions.assertThrows(StyxException.class, () -> accessTokenFilter.checkRestrictions(masterToken, "pis"));
        try {
            accessTokenFilter.checkRestrictions(masterToken, "pis");
        } catch (StyxException exception) {
            ResponseEntity response = exception.getResponseEntity();
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getReasonPhrase(), response.getMessage());
            Assertions.assertEquals(ResponseConstant.STYX_MASTER_TOKEN_RESTRICTED.getStatusCode(), response.getCode().getStatusCode());
            Assertions.assertEquals(ResponseCategory.ERROR, response.getCategory());
            Assertions.assertEquals(ResponseOrigin.STYX, response.getOrigin());
        }
    }

    @Test
    void testMasterTokenRestrictions() {
        MasterToken masterToken = prepareMasterToken("pis", 5);
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter();
        Assertions.assertDoesNotThrow(() -> accessTokenFilter.checkRestrictions(masterToken, "pis"));
    }

    /**
     * helper method which sets up a masterToken object with given service Type and maxUsage
     *
     * @param serviceType string service type for which maxUsages will be set
     * @param maxUsage    amount of maxUsage for serviceType
     * @return object of MasterToken
     */
    private MasterToken prepareMasterToken(String serviceType, Integer maxUsage) {
        MasterToken masterToken = new MasterToken();
        MasterTokenRestriction masterTokenRestriction = new MasterTokenRestriction();
        masterTokenRestriction.setMaxUsages(maxUsage);
        Map<String, MasterTokenRestriction> restrictionMap = new HashMap<>();
        restrictionMap.put(serviceType, masterTokenRestriction);
        masterToken.setRestrictions(restrictionMap);
        return masterToken;
    }
}