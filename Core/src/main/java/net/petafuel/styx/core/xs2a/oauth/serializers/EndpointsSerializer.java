package net.petafuel.styx.core.xs2a.oauth.serializers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EndpointsSerializer implements JsonDeserializer<HashMap> {

    @Override
    public HashMap<String, String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        HashMap<String, String> endpoints = new HashMap<>();
        JsonObject object = jsonElement.getAsJsonObject();
        object.entrySet().iterator();
        Iterator<Map.Entry<String, JsonElement>> iterator = object.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> item = iterator.next();
            endpoints.put(item.getKey(), item.getValue().toString().replaceAll("^\"|\"$", ""));
        }
        return endpoints;
    }
}
