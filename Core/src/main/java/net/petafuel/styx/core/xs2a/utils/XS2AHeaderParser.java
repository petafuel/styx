package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.exceptions.XS2AHeaderParserException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XS2AHeaderParser {
    private XS2AHeaderParser() {
    }

    public static void parse(XS2ARequest annotated) {
        try {
            mapFields(annotated, annotated);
        } catch (IllegalAccessException e) {
            throw new XS2AHeaderParserException(e.getMessage(), e);
        }
    }

    private static void mapFields(Object o, XS2ARequest xs2aRequest) throws IllegalAccessException {
        Class<?> c = o.getClass();
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(c.getDeclaredFields()));
        List<Field> parentFields = Arrays.asList(c.getSuperclass().getDeclaredFields());
        fields.addAll(parentFields);
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(XS2AHeader.class)) {
                if (field.getAnnotation(XS2AHeader.class).nested() && field.get(o) != null) {
                    mapFields(field.get(o), xs2aRequest);
                } else {
                    try {
                        if (field.get(o) != null && !String.valueOf(field.get(o)).isEmpty()) {
                            xs2aRequest.setHeader(field.getAnnotation(XS2AHeader.class).value(), String.valueOf(field.get(o)));
                        }
                    } catch (IllegalAccessException e) {
                        throw new XS2AHeaderParserException(e.getMessage(), e);
                    }
                }
            }
        }
    }
}