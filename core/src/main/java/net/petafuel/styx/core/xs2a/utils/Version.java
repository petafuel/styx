package net.petafuel.styx.core.xs2a.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Version {
    List<Integer> versions;

    public Version(String versionStr) {
        versions = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(versionStr, ".");
        while (tokenizer.hasMoreTokens()) {
            versions.add(Integer.parseInt(tokenizer.nextToken()));
        }
    }

    public Integer getMajor() {
        try {
            return versions.get(0);
        } catch (
                IndexOutOfBoundsException notAvailable) {
            return null;
        }
    }

    public Integer getMinor() {
        try {
            return versions.get(1);
        } catch (
                IndexOutOfBoundsException notAvailable) {
            return null;
        }
    }

    public Integer getPatch() {
        try {
            return versions.get(2);
        } catch (
                IndexOutOfBoundsException notAvailable) {
            return null;
        }
    }
}
