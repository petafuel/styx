package net.petafuel.styx.api.util;

import com.google.gson.JsonElement;
import net.petafuel.styx.api.exception.ResponseCategory;
import net.petafuel.styx.api.exception.ResponseConstant;
import net.petafuel.styx.api.exception.ResponseEntity;
import net.petafuel.styx.api.exception.ResponseOrigin;
import net.petafuel.styx.api.exception.StyxException;
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

    public JsonElement getOption(String io, String key) {
        ImplementerOption option = implementerOptions.get(io);
        if (option == null) {
            throw new StyxException(new ResponseEntity(String.format("ImplementerOption list does not contain option=%s for key=%s", io, key),
                    ResponseConstant.INTERNAL_SERVER_ERROR, ResponseCategory.ERROR, ResponseOrigin.STYX));
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
