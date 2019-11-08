package net.petafuel.styx.core.xs2a.utils.jsepa;

import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class CdtTrfTxInf extends CreditTransferTransactionInformation {

    @Element(name = "InstrId", required = false)
    @Path("PmtId")
    String instrId;

    public CdtTrfTxInf(){

    }

    public String getInstrId() {
        return instrId;
    }

    public void setInstrId(String instrId) {
        this.instrId = instrId;
    }
}
