package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

/**
 * This contains all account details
 * opposite to the AccountReference, which only contains the identifier for an account
 * This class together with the AccountReference replaces the non-compliant Account model
 *
 * @see AccountReference
 */
public class AccountDetails implements XS2AResponse {
    private String resourceId;
    private String iban;
    private String bban;
    private String msisdn;
    private Currency currency;
    private String ownerName;
    private String name;
    private String displayName;
    private String product;
    @JsonbProperty("cashAccountType")
    private CashAccountType cashAccountType;
    private String status;
    private String bic;
    private String linkedAccounts;
    private String usage;
    private String details;
    private List<Balance> balances;
    private Links links;

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

    public String getBban() {
        return bban;
    }

    public void setBban(String bban) {
        this.bban = bban;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public CashAccountType getCashAccountType() {
        return cashAccountType;
    }

    public void setCashAccountType(CashAccountType cashAccountType) {
        this.cashAccountType = cashAccountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(String linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    @JsonbProperty("links")
    public Links getLinks() {
        return links;
    }

    @JsonbProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }
}