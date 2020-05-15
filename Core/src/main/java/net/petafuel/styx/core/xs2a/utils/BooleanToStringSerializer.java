package net.petafuel.styx.core.xs2a.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BooleanToStringSerializer implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        return element.getAsString().equals("true");
    }

    @Override
    public JsonElement serialize(Boolean value, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(Boolean.TRUE.equals(value));
    }
}
