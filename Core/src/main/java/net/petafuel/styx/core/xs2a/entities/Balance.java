package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;

public class Balance {

    public enum Currency {
        EUR
    }

    public enum Type {
        CLOSING_BOOKED
    }

    private Currency currency;

    private float amount;

    private Type type;

    private Date referenceDate;

}
