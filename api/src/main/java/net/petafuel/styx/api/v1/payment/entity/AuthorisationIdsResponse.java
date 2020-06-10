package net.petafuel.styx.api.v1.payment.entity;

import java.util.List;

public class AuthorisationIdsResponse {

    private List<String> authorisationIds;

    public List<String> getAuthorisationIds() {
        return authorisationIds;
    }

    public void setAuthorisationIds(List<String> authorisationIds) {
        this.authorisationIds = authorisationIds;
    }
}
