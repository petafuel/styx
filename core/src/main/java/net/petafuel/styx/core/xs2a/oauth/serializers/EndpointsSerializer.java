package net.petafuel.styx.core.xs2a.oauth.serializers;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EndpointsSerializer implements JsonbDeserializer<HashMap<String, String>> {

    @Override
    public HashMap<String, String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        HashMap<String, String> endpoints = new HashMap<>();
        JsonObject object = jsonParser.getObject();

        for (Map.Entry<String, JsonValue> item : object.entrySet()) {
            endpoints.put(item.getKey(), item.getValue().toString().replaceAll("^\"|\"$", ""));
        }
        return endpoints;
    }
}