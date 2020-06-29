package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISODateDeserializer implements JsonbDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());
        simpleDateFormat.setLenient(false);
        try {
            return simpleDateFormat.parse(jsonParser.getString());
        } catch (ParseException e) {
            throw new SerializerException("Wrong dateformat, must be " + XS2AJsonKeys.DATE_FORMAT.value(), e);
        }
    }
}
