package net.petafuel.styx.core.banklookup;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XS2AStandardTest {

    @Test
    void isAISImplemented() {
        XS2AStandard standard = new XS2AStandard();
        Assert.assertFalse(standard.isAISImplemented());
    }

    @Test
    void isPISImplemented() {
        XS2AStandard standard = new XS2AStandard();
        Assert.assertFalse(standard.isPISImplemented());
    }

    @Test
    void isPIISImplemented() {
        XS2AStandard standard = new XS2AStandard();
        Assert.assertFalse(standard.isPIISImplemented());
    }

    @Test
    void isCSImplemented() {
        XS2AStandard standard = new XS2AStandard();
        Assert.assertFalse(standard.isCSImplemented());
    }
}