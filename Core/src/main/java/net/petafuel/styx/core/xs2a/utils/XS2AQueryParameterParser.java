package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;
import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.exceptions.XS2AHeaderParserException;

import java.lang.reflect.Field;

public class XS2AQueryParameterParser {

    public XS2AQueryParameterParser() {
    }

    public static void parse(XS2AGetRequest annotated) {
        try {
            mapFields(annotated, annotated);
        } catch (IllegalAccessException e) {
            throw new XS2AHeaderParserException(e.getMessage());
        }
    }

    private static void mapFields(Object o, XS2AGetRequest request) throws IllegalAccessException {
        Class<?> c = o.getClass();

        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(XS2AQueryParameter.class)) {
                try {
                    if (field.get(o) != null && !String.valueOf(field.get(o)).isEmpty()) {
                        request.setQueryParameter(field.getAnnotation(XS2AQueryParameter.class).value(), String.valueOf(field.get(o)));
                    }
                } catch (IllegalAccessException e) {
                    throw new XS2AHeaderParserException(e.getMessage());
                }
            }
        }
    }
}
