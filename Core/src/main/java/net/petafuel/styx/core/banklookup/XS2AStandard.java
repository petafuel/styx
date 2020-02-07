package net.petafuel.styx.core.banklookup;

import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.xs2a.contracts.AISInterface;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.PIISInterface;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;

public class XS2AStandard {

    private AISInterface ais;
    private PISInterface pis;
    private PIISInterface piis;
    private CSInterface cs;
    private Aspsp aspsp;

    public XS2AStandard(AISInterface ais, PISInterface pis, PIISInterface piis, CSInterface cs) {
        this.ais = ais;
        this.pis = pis;
        this.piis = piis;
        this.cs = cs;
    }

    public XS2AStandard() {
    }

    public Aspsp getAspsp() {
        return aspsp;
    }

    public void setAspsp(Aspsp aspsp) {
        this.aspsp = aspsp;
    }

    public Boolean isAISImplemented() {
        return this.ais != null;
    }

    public Boolean isPISImplemented() {
        return this.pis != null;
    }

    public Boolean isPIISImplemented() {
        return this.piis != null;
    }

    public Boolean isCSImplemented() {
        return this.cs != null;
    }

    public AISInterface getAis() {
        return ais;
    }

    public void setAis(AISInterface ais) {
        this.ais = ais;
    }

    public CSInterface getCs() {
        return cs;
    }

    public void setCs(CSInterface cs) {
        this.cs = cs;
    }

    public PIISInterface getPiis() {
        return piis;
    }

    public void setPiis(PIISInterface piis) {
        this.piis = piis;
    }

    public PISInterface getPis() {
        return pis;
    }

    public void setPis(PISInterface pis) {
        this.pis = pis;
    }
}
