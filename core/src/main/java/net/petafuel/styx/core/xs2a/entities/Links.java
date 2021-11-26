package net.petafuel.styx.core.xs2a.entities;

import net.petafuel.styx.core.xs2a.entities.serializers.HrefTypeDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.util.EnumMap;
import java.util.Map;

/**
 * Links container, this will hold all generic links that an XS2A Interface might return and makes them accessable
 * through the LinkType
 */
public class Links {
    private static final Logger LOG = LogManager.getLogger(Links.class);

    @JsonbTransient
    private final Map<LinkType, Href> urlMapping = new EnumMap<>(LinkType.class);

    public Map<LinkType, Href> getUrlMapping() {
        return urlMapping;
    }

    @JsonbProperty("scaRedirect")
    public Href getScaRedirect() {
        return urlMapping.get(LinkType.SCA_REDIRECT);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("scaRedirect")
    public void setScaRedirect(Href scaRedirect) {
        urlMapping.put(LinkType.SCA_REDIRECT, scaRedirect);
    }

    @JsonbProperty("scaOAuth")
    public Href getScaOAuth() {
        return urlMapping.get(LinkType.SCA_OAUTH);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("scaOAuth")
    public void setScaOAuth(Href scaOAuth) {
        urlMapping.put(LinkType.SCA_OAUTH, scaOAuth);
    }

    @JsonbProperty("confirmation")
    public Href getConfirmation() {
        return urlMapping.get(LinkType.CONFIRMATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("confirmation")
    public void setConfirmation(Href confirmation) {
        urlMapping.put(LinkType.CONFIRMATION, confirmation);
    }

    @JsonbProperty("startAuthorisation")
    public Href getStartAuthorisation() {
        return urlMapping.get(LinkType.START_AUTHORISATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("startAuthorisation")
    public void setStartAuthorisation(Href startAuthorisation) {
        urlMapping.put(LinkType.START_AUTHORISATION, startAuthorisation);
    }

    @JsonbProperty("startAuthorisationWithPsuIdentification")
    public Href getStartAuthorisationWithPsuIdentification() {
        return urlMapping.get(LinkType.AUTHORISATION_WITH_PSU_IDENTIFICATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("startAuthorisationWithPsuIdentification")
    public void setStartAuthorisationWithPsuIdentification(Href startAuthorisationWithPsuIdentification) {
        urlMapping.put(LinkType.AUTHORISATION_WITH_PSU_IDENTIFICATION, startAuthorisationWithPsuIdentification);
    }

    /**
     * Handle possible typo from Berlin Group spec in case ASPSP implemented it.
     *
     * @param startAuthorisationWithPsuIdentification Href Link
     * @deprecated This should only be viable until the typo is fixed in the specification
     */
    @Deprecated
    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("startAuthorisationWithPsuIdentfication")
    public void setStartAuthorisationWithPsuIdentificationWithTypo(Href startAuthorisationWithPsuIdentification) {
        LOG.warn("ASPSP used incorrect method 'startAuthorisationWithPsuIdentfication' (containing typo).");
        urlMapping.put(LinkType.AUTHORISATION_WITH_PSU_IDENTIFICATION, startAuthorisationWithPsuIdentification);
    }

    @JsonbProperty("startAuthorisationWithProprietaryData")
    public Href startAuthorisationWithProprietaryData() {
        return urlMapping.get(LinkType.START_AUTHORISATION_WITH_PROPRIETARY_DATA);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("startAuthorisationWithProprietaryData")
    public void setStartAuthorisationWithProprietaryData(Href startAuthorisationWithProprietaryData) {
        urlMapping.put(LinkType.START_AUTHORISATION_WITH_PROPRIETARY_DATA, startAuthorisationWithProprietaryData);
    }

    @JsonbProperty("updateProprietaryData")
    public Href getUpdateProprietaryData() {
        return urlMapping.get(LinkType.UPDATE_PROPRIETARY_DATA);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("updateProprietaryData")
    public void setUpdateProprietaryData(Href updateProprietaryData) {
        urlMapping.put(LinkType.UPDATE_PROPRIETARY_DATA, updateProprietaryData);
    }

    @JsonbProperty("startAuthorisationWithPsuAuthentication")
    public Href getStartAuthorisationWithPsuAuthentication() {
        return urlMapping.get(LinkType.AUTHORISATION_WITH_PSU_AUTHENTICATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("startAuthorisationWithPsuAuthentication")
    public void setStartAuthorisationWithPsuAuthentication(Href startAuthorisationWithPsuAuthentication) {
        urlMapping.put(LinkType.AUTHORISATION_WITH_PSU_AUTHENTICATION, startAuthorisationWithPsuAuthentication);
    }

    @JsonbProperty("updatePsuAuthentication")
    public Href getUpdatePsuAuthentication() {
        return urlMapping.get(LinkType.UPDATE_PSU_AUTHENTICATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("updatePsuAuthentication")
    public void setUpdatePsuAuthentication(Href updatePsuAuthentication) {
        urlMapping.put(LinkType.UPDATE_PSU_AUTHENTICATION, updatePsuAuthentication);
    }

    @JsonbProperty("updateAdditionalPsuAuthentication")
    public Href getUpdateAdditionalPsuAuthentication() {
        return urlMapping.get(LinkType.UPDATE_ADDITIONAL_PSU_AUTHENTICATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("updateAdditionalPsuAuthentication")
    public void setUpdateAdditionalPsuAuthentication(Href updateAdditionalPsuAuthentication) {
        urlMapping.put(LinkType.UPDATE_ADDITIONAL_PSU_AUTHENTICATION, updateAdditionalPsuAuthentication);
    }

    @JsonbProperty("startAuthorisationWithEncryptedPsuAuthentication")
    public Href getStartAuthorisationWithEncryptedPsuAuthentication() {
        return urlMapping.get(LinkType.AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("startAuthorisationWithEncryptedPsuAuthentication")
    public void setStartAuthorisationWithEncryptedPsuAuthentication(Href startAuthorisationWithEncryptedPsuAuthentication) {
        urlMapping.put(LinkType.AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION, startAuthorisationWithEncryptedPsuAuthentication);
    }

    @JsonbProperty("updateEncryptedPsuAuthentication")
    public Href getUpdateEncryptedPsuAuthentication() {
        return urlMapping.get(LinkType.UPDATE_ENCRYPTED_PSU_AUTHENTICATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("updateEncryptedPsuAuthentication")
    public void setUpdateEncryptedPsuAuthentication(Href updateEncryptedPsuAuthentication) {
        urlMapping.put(LinkType.UPDATE_ENCRYPTED_PSU_AUTHENTICATION, updateEncryptedPsuAuthentication);
    }

    @JsonbProperty("updateAdditionalEncryptedPsuAuthentication")
    public Href getUpdateAdditionalEncryptedPsuAuthentication() {
        return urlMapping.get(LinkType.UPDATE_ENCRYPTED_ADDITIONAL_PSU_AUTHENTICATION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("updateAdditionalEncryptedPsuAuthentication")
    public void setUpdateAdditionalEncryptedPsuAuthentication(Href updateAdditionalEncryptedPsuAuthentication) {
        urlMapping.put(LinkType.UPDATE_ENCRYPTED_ADDITIONAL_PSU_AUTHENTICATION, updateAdditionalEncryptedPsuAuthentication);
    }

    @JsonbProperty("startAuthorisationWithAuthenticationMethodSelection")
    public Href getStartAuthorisationWithAuthenticationMethodSelection() {
        return urlMapping.get(LinkType.AUTHORISATION_WITH_METHOD_SELECTION);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("startAuthorisationWithAuthenticationMethodSelection")
    public void setStartAuthorisationWithAuthenticationMethodSelection(Href startAuthorisationWithAuthenticationMethodSelection) {
        urlMapping.put(LinkType.AUTHORISATION_WITH_METHOD_SELECTION, startAuthorisationWithAuthenticationMethodSelection);
    }

    /**
     * Handle possible typo from Berlin Group spec in case ASPSP implemented it.
     *
     * @param startAuthorisationWithAuthenticationMethodSelection Href Link
     * @deprecated This should only be viable until the typo is fixed in the specification
     */
    @Deprecated
    @JsonbProperty("startAuthorisationWithAuthentciationMethodSelection")
    public void setStartAuthorisationWithAuthenticationMethodSelectionWithTypo(Href startAuthorisationWithAuthenticationMethodSelection) {
        LOG.warn("ASPSP used incorrect method 'startAuthorisationWithAuthentciationMethodSelection' (containing typo).");
        urlMapping.put(LinkType.AUTHORISATION_WITH_METHOD_SELECTION, startAuthorisationWithAuthenticationMethodSelection);
    }

    @JsonbProperty("selectAuthenticationMethod")
    public Href getSelectAuthenticationMethod() {
        return urlMapping.get(LinkType.SELECT_AUTHENTICATION_METHOD);
    }

    @JsonbProperty("selectAuthenticationMethod")
    public void setSelectAuthenticationMethod(Href selectAuthenticationMethod) {
        urlMapping.put(LinkType.SELECT_AUTHENTICATION_METHOD, selectAuthenticationMethod);
    }

    @JsonbProperty("startAuthorisationWithTransactionAuthorisation")
    public Href getStartAuthorisationWithTransactionAuthorisation() {
        return urlMapping.get(LinkType.AUTHORISATION_WITH_TRANSACTION_AUTHORISATION);
    }

    @JsonbProperty("startAuthorisationWithTransactionAuthorisation")
    public void setStartAuthorisationWithTransactionAuthorisation(Href startAuthorisationWithTransactionAuthorisation) {
        urlMapping.put(LinkType.AUTHORISATION_WITH_TRANSACTION_AUTHORISATION, startAuthorisationWithTransactionAuthorisation);
    }

    @JsonbProperty("authoriseTransaction")
    public Href getAuthoriseTransaction() {
        return urlMapping.get(LinkType.AUTHORISE_TRANSACTION);
    }

    @JsonbProperty("authoriseTransaction")
    public void setAuthoriseTransaction(Href authoriseTransaction) {
        urlMapping.put(LinkType.AUTHORISE_TRANSACTION, authoriseTransaction);
    }

    @JsonbProperty("self")
    public Href getSelf() {
        return urlMapping.get(LinkType.SELF);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("self")
    public void setSelf(Href self) {
        urlMapping.put(LinkType.SELF, self);
    }

    @JsonbProperty("status")
    public Href getStatus() {
        return urlMapping.get(LinkType.STATUS);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("status")
    public void setStatus(Href status) {
        urlMapping.put(LinkType.STATUS, status);
    }

    public Href getScaStatus() {
        return urlMapping.get(LinkType.SCA_STATUS);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("scaStatus")
    public void setScaStatus(Href scaStatus) {
        urlMapping.put(LinkType.SCA_STATUS, scaStatus);
    }

    @JsonbProperty("account")
    public Href getAccount() {
        return urlMapping.get(LinkType.ACCOUNT);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("account")
    public void setAccount(Href account) {
        urlMapping.put(LinkType.ACCOUNT, account);
    }

    @JsonbProperty("balances")
    public Href getBalances() {
        return urlMapping.get(LinkType.BALANCES);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("balances")
    public void setBalances(Href balances) {
        urlMapping.put(LinkType.BALANCES, balances);
    }

    @JsonbProperty("transactions")
    public Href getTransactions() {
        return urlMapping.get(LinkType.TRANSACTIONS);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    public void setTransactions(Href transactions) {
        urlMapping.put(LinkType.TRANSACTIONS, transactions);
    }

    @JsonbProperty("cardAccount")
    public Href getCardAccount() {
        return urlMapping.get(LinkType.CARD_ACCOUNTS);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("cardAccount")
    public void setCardAccount(Href cardAccount) {
        urlMapping.put(LinkType.CARD_ACCOUNTS, cardAccount);
    }

    @JsonbProperty("cardTransactions")
    public Href getCardTransactions() {
        return urlMapping.get(LinkType.CARD_TRANSACTIONS);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("cardTransactions")
    public void setCardTransactions(Href cardTransactions) {
        urlMapping.put(LinkType.CARD_TRANSACTIONS, cardTransactions);
    }

    @JsonbProperty("transactionDetails")
    public Href getTransactionDetails() {
        return urlMapping.get(LinkType.TRANSACTION_DETAILS);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("transactionDetails")
    public void setTransactionDetails(Href transactionDetails) {
        urlMapping.put(LinkType.TRANSACTION_DETAILS, transactionDetails);
    }

    @JsonbProperty("first")
    public Href getFirst() {
        return urlMapping.get(LinkType.FIRST);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("first")
    public void setFirst(Href first) {
        urlMapping.put(LinkType.FIRST, first);
    }

    @JsonbProperty("next")
    public Href getNext() {
        return urlMapping.get(LinkType.NEXT);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("next")
    public void setNext(Href next) {
        urlMapping.put(LinkType.NEXT, next);
    }

    @JsonbProperty("previous")
    public Href getPrevious() {
        return urlMapping.get(LinkType.PREVIOUS);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("previous")
    public void setPrevious(Href previous) {
        urlMapping.put(LinkType.PREVIOUS, previous);
    }

    @JsonbProperty("last")
    public Href getLast() {
        return urlMapping.get(LinkType.LAST);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("last")
    public void setLast(Href last) {
        urlMapping.put(LinkType.LAST, last);
    }

    @JsonbProperty("download")
    public Href getDownload() {
        return urlMapping.get(LinkType.DOWNLOAD);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("download")
    public void setDownload(Href download) {
        urlMapping.put(LinkType.DOWNLOAD, download);
    }

    @JsonbProperty("authorization_endpoint")
    public Href getAuthorizationEndpoint() {
        return urlMapping.get(LinkType.AUTHORIZATION_ENDPOINT);
    }

    @JsonbTypeDeserializer(HrefTypeDeserializer.class)
    @JsonbProperty("authorization_endpoint")
    public void setAuthorizationEndpoint(Href authorizationEndpoint) {
        urlMapping.put(LinkType.AUTHORIZATION_ENDPOINT, authorizationEndpoint);
    }

    public static class Href {
        @JsonbProperty("href")
        private String url;
        @JsonbTransient
        private LinkType linkType;

        public Href(String url, LinkType linkType) {
            this.url = url;
            this.linkType = linkType;
        }

        public Href() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public LinkType getLinkType() {
            return linkType;
        }

        public void setLinkType(LinkType linkType) {
            this.linkType = linkType;
        }
    }
}
