package net.petafuel.styx.core.xs2a.entities;

import java.util.List;

/**
 * Defines additional AccountReferences for the AccountAccess
 *
 * @See AccountAccess
 */
public class AdditionalInformationAccess {
    List<AccountReference> ownerName;

    public List<AccountReference> getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(List<AccountReference> ownerName) {
        this.ownerName = ownerName;
    }
}
