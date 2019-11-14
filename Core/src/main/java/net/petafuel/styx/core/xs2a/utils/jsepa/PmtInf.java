package net.petafuel.styx.core.xs2a.utils.jsepa;

import net.petafuel.jsepa.model.PaymentInstructionInformation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class PmtInf extends PaymentInstructionInformation {

    @Element(name = "BtchBookg", required = false)
    Boolean batchBooking;

    public PmtInf(){

    }

    public boolean isBatchBooking() {
        return batchBooking;
    }

    public void setBatchBooking(boolean batchBooking) {
        this.batchBooking = batchBooking;
    }
}
