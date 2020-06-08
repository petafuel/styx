package net.petafuel.styx.api.v1.account.control;


import net.petafuel.styx.api.v1.account.entity.AccountDetailsAdapted;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapt BerlinGroup AccountDetails to Styx Model
 */
public class AccountListResponseAdapter {
    private List<AccountDetailsAdapted> accounts;

    public AccountListResponseAdapter(List<AccountDetails> accounts) {
        this.accounts = new ArrayList<>();
        accounts.forEach(accountDetails -> {
            AccountDetailsAdapted accountDetailsAdapted = new AccountDetailsAdapted();
            accountDetailsAdapted.setCurrency(accountDetails.getCurrency());
            accountDetailsAdapted.setIban(accountDetails.getIban());
            accountDetailsAdapted.setLinks(accountDetails.getLinks());
            accountDetailsAdapted.setOwnerName(accountDetails.getOwnerName());
            accountDetailsAdapted.setProduct(accountDetails.getProduct());
            accountDetailsAdapted.setResourceId(accountDetails.getResourceId());
            this.accounts.add(accountDetailsAdapted);
        });
    }

    /**
     * Default constructor for json-binding
     */
    public AccountListResponseAdapter() {
    }

    public List<AccountDetailsAdapted> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDetailsAdapted> accounts) {
        this.accounts = accounts;
    }

}
