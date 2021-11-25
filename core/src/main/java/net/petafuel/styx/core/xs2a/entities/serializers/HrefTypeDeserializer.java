package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
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

    @Override
    public Links.Href deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

        Jsonb jsonb = JsonbBuilder.create();
        JsonValue parsedValue = parser.getValue();

        if (parsedValue instanceof JsonObject) {
            return jsonb.fromJson(parsedValue.toString(), Links.Href.class);
        } else {
            LinkType linkType = null;
            if (parser instanceof JsonbParser) {
                JsonbParser jsonbParser = (JsonbParser) parser;
                linkType = LinkType.getByString(jsonbParser.getCurrentLevel().getLastKeyName());
            }
            return new Links.Href(parsedValue.toString().replace("\"", ""), linkType);
        }
    }
}
