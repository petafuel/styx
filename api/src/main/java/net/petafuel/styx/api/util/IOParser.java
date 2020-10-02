package net.petafuel.styx.api.util;

import net.petafuel.styx.api.util.io.entities.ImplementerOptionException;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;

import java.util.Map;

/**
 * Implementer Option Parser
 */
public final class IOParser {
    private final Map<String, ImplementerOption> implementerOptions;

    public IOParser(Aspsp aspsp) {
        implementerOptions = aspsp.getConfig().getImplementerOptions();
    }

    public ImplementerOption get(String io) {
        return implementerOptions.get(io);
    }

    public Boolean getOption(String io, String key) throws ImplementerOptionException {
        ImplementerOption option = implementerOptions.get(io);
        if (option == null) {
            throw new ImplementerOptionException(String.format("ImplementerOption list does not contain option=%s for key=%s", io, key));
        }
        return option.getOptions().get(key);
    }

    public final class Option {
        public static final String REQUIRED = "required";
        public static final String AVAILABLE = "available";

        private Option() {
        }
    }
}
