package net.petafuel.styx.core.banklookup;


import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SimpleBankLookUpTest {

    @Test
    @Tag("integration")
    public void simpleBankLookUp () {
        SimpleBankLookUp simpleBankLookUp = new SimpleBankLookUp();
        Assert.assertTrue(simpleBankLookUp instanceof BankLookUpInterface);
    }
}
