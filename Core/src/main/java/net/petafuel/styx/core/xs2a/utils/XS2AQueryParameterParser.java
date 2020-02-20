package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.exceptions.XS2AHeaderParserException;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class XS2AQueryParameterParser {

    private XS2AQueryParameterParser() {
        //To hide constructor from static Parser
    }

    public static void parse(XS2ARequest annotated) {
        mapFields(annotated, annotated);
    }

    private static void mapFields(Object o, XS2ARequest request) {
        Class<?> c = o.getClass();
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(c.getDeclaredFields()));
        List<Field> parentFields = XS2AHeaderParser.getParentAllFields(c);
        fields.addAll(parentFields);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(XS2AQueryParameter.class) && field.get(o) != null) {
                    if (field.getAnnotation(XS2AQueryParameter.class).nested()) {
                        mapFields(field.get(o), request);
                    } else if (!String.valueOf(field.get(o)).isEmpty()) {
                        if (field.get(o) instanceof Date) {
                            String isoDate = sdf.format(field.get(o));
                            request.addQueryParameter(field.getAnnotation(XS2AQueryParameter.class).value(), isoDate);
                        } else {
                            request.addQueryParameter(field.getAnnotation(XS2AQueryParameter.class).value(), String.valueOf(field.get(o)));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new XS2AHeaderParserException(e.getMessage(), e);
        }
    }
}
