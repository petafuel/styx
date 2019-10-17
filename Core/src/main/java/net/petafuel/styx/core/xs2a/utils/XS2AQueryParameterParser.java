package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;
import net.petafuel.styx.core.xs2a.exceptions.XS2AHeaderParserException;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XS2AQueryParameterParser {

    private XS2AQueryParameterParser() {
        //To hide constructor from static Parser
    }

    public static void parse(XS2AGetRequest annotated) {
        mapFields(annotated, annotated);
    }

    private static void mapFields(Object o, XS2AGetRequest request) {
        Class<?> c = o.getClass();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(XS2AQueryParameter.class)) {
                try {
                    if (field.get(o) != null && !String.valueOf(field.get(o)).isEmpty()) {
                        if (field.get(o) instanceof Date) {
                            String isoDate = sdf.format(field.get(o));
                            request.setQueryParameter(field.getAnnotation(XS2AQueryParameter.class).value(), isoDate);
                        } else {
                            request.setQueryParameter(field.getAnnotation(XS2AQueryParameter.class).value(), String.valueOf(field.get(o)));
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new XS2AHeaderParserException(e.getMessage(), e);
                }
            }
        }
    }
}
