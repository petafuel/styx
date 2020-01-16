package net.petafuel.styx.core.banklookup;

import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;

public interface BankLookUpInterface {
    XS2AStandard getBankByBIC(String bic) throws BankNotFoundException, BankLookupFailedException;
}
