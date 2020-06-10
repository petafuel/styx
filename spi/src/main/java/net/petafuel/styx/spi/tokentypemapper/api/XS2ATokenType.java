package net.petafuel.styx.spi.tokentypemapper.api;

import java.util.Arrays;

public enum XS2ATokenType {
    AIS("ais"),
    PIS("pis"),
    PIIS("piis"),
    AISPIS("aispis");

    private final String value;

    XS2ATokenType(String value) {
        this.value = value;
    }

    public static XS2ATokenType getByString(String name) {
        return Arrays.asList(values()).parallelStream().filter(enumEntry -> enumEntry.value.equals(name)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return value;
    }
}
