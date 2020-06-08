package net.petafuel.styx.core.xs2a.utils;

import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.exceptions.XS2AHeaderParserException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                if (!field.isAnnotationPresent(XS2AQueryParameter.class)) {
                    continue;
                }
                Method getter = new PropertyDescriptor(field.getName(), o.getClass()).getReadMethod();
                if (getter.invoke(o) != null) {
                    if (field.getAnnotation(XS2AQueryParameter.class).nested()) {
                        mapFields(getter.invoke(o), request);
                    } else if (!String.valueOf(getter.invoke(o)).isEmpty()) {
                        if (getter.invoke(o) instanceof Date) {
                            String isoDate = sdf.format(getter.invoke(o));
                            request.addQueryParameter(field.getAnnotation(XS2AQueryParameter.class).value(), isoDate);
                        } else {
                            request.addQueryParameter(field.getAnnotation(XS2AQueryParameter.class).value(), String.valueOf(getter.invoke(o)));
                        }
                    }
                }
            }
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            throw new XS2AHeaderParserException(e.getMessage(), e);
        }
    }
}
