package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import java.util.List;

public class GetAuthorisationResponse {
    List<String> authorisationIds;

    public List<String> getAuthorisationIds() {
        return authorisationIds;
    }

    public void setAuthorisationIds(List<String> authorisationIds) {
        this.authorisationIds = authorisationIds;
    }
}
