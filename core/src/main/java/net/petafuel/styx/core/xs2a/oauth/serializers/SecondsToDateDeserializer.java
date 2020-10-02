package net.petafuel.styx.core.xs2a.oauth.serializers;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.Date;

public class SecondsToDateDeserializer implements JsonbDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        Date date = new Date();
        date.setTime(date.getTime() + (jsonParser.getLong() * 1000));
        return date;
    }
}