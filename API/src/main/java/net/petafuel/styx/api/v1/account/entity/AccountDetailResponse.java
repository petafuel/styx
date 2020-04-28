package net.petafuel.styx.api.v1.account.entity;

import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.Links;

/**
 * This Container crops the AccountDetails Model to fit the Styx REST Interface definition
 *
 * @see AccountDetails
 */
public class AccountDetailResponse {
    private AccountJson account;

    public AccountDetailResponse(AccountDetails accountDetails) {
        account = new AccountJson(accountDetails.getResourceId(),
                accountDetails.getIban(),
                accountDetails.getCurrency().name(),
                accountDetails.getOwnerName(),
                accountDetails.getProduct(),
                accountDetails.getLinks());
    }

    /**
     * @deprecated default constructor for json binding
     */
    @Deprecated
    public AccountDetailResponse() {
        //default constructor for json binding
    }

    public AccountJson getAccount() {
        return account;
    }

    public void setAccount(AccountJson account) {
        this.account = account;
    }

    public static class AccountJson {
        private String resourceId;
        private String iban;
        private String currency;
        private String ownerName;
        private String product;
        private Links links;

        public AccountJson(String resourceId, String iban, String currency, String ownerName, String product, Links links) {
            this.resourceId = resourceId;
            this.iban = iban;
            this.currency = currency;
            this.ownerName = ownerName;
            this.product = product;
            this.links = links;
        }

        /**
         * @deprecated default constructor for json binding
         */
        @Deprecated
        public AccountJson() {
            //default constructor for json binding
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getIban() {
            return iban;
        }

        public void setIban(String iban) {
            this.iban = iban;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public Links getLinks() {
            return links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }
    }
}
