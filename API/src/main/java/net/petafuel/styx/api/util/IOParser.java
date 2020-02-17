package net.petafuel.styx.api.util;

import com.google.gson.JsonElement;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;

import java.util.Map;

/**
 * Implementer Option Parser
 */
public final class IOParser {
    private Map<String, ImplementerOption> implementerOptions;

    public IOParser(Aspsp aspsp) {
        implementerOptions = aspsp.getConfig().getImplementerOptions();
    }

    public ImplementerOption get(String io) {
        return implementerOptions.get(io);
    }

    public JsonElement getOption(String io, String key) {
        return implementerOptions.get(io).getOptions().get(key);
    }

    public final class Option {
        public static final String REQUIRED = "required";
        public static final String AVAILABLE = "available";

        private Option() {
        }
    }
}
