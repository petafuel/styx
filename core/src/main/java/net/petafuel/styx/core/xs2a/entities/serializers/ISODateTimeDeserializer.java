package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISODateTimeDeserializer implements JsonbDeserializer<Date> {
    private static final String FORMAT = "yyyy-MM-ddThh:mm:ss.sssZ";

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
        simpleDateFormat.setLenient(false);
        try {
            return simpleDateFormat.parse(jsonParser.getString());
        } catch (ParseException e) {
            throw new SerializerException("Wrong datetimeformat, must be " + FORMAT, e);
        }
    }
}