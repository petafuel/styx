package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.yasson.internal.JsonbParser;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

public class HrefTypeDeserializer implements JsonbDeserializer<Links.Href> {
    private static final Logger LOG = LogManager.getLogger(HrefTypeDeserializer.class);

    @Override
    public Links.Href deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonValue parsedValue = parser.getValue();
        Links.Href href = new Links.Href();

        if (parsedValue instanceof JsonObject) {
            try (Jsonb jsonb = JsonbBuilder.create()) {
                href = jsonb.fromJson(parsedValue.toString(), Links.Href.class);
            } catch (Exception e) {
                LOG.error("Could not initiate jsonb instance. ErrorMessage={}", e.getMessage());
            }
        } else {
            LinkType linkType = null;
            if (parser instanceof JsonbParser) {
                JsonbParser jsonbParser = (JsonbParser) parser;
                linkType = LinkType.getByString(jsonbParser.getCurrentLevel().getLastKeyName());
            }
            href = new Links.Href(parsedValue.toString().replace("\"", ""), linkType);
        }
        return href;
    }
}
