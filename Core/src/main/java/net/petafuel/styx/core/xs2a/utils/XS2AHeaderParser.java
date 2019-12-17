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
        mapFields(annotated, annotated);
    }

    private static void mapFields(Object o, XS2ARequest xs2aRequest) {
        Class<?> c = o.getClass();
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(c.getDeclaredFields()));
        List<Field> parentFields = getParentAllFields(c);
        fields.addAll(parentFields);
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(XS2AHeader.class) && field.get(o) != null) {
                    if (field.getAnnotation(XS2AHeader.class).nested()) {
                        mapFields(field.get(o), xs2aRequest);
                    } else {
                        if (!String.valueOf(field.get(o)).isEmpty()) {
                            xs2aRequest.addHeader(field.getAnnotation(XS2AHeader.class).value(), String.valueOf(field.get(o)));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new XS2AHeaderParserException(e.getMessage(), e);
        }
    }

    /**
     * Reflection will go up to native Java Object an returns all available Fields
     *
     * @param child
     * @return
     */
    public static List<Field> getParentAllFields(Class<?> child) {
        List<Field> allFields = new ArrayList<>();
        if (child.getSuperclass().getDeclaredFields().length != 0) {
            allFields.addAll(Arrays.asList(child.getSuperclass().getDeclaredFields()));
            allFields.addAll(getParentAllFields(child.getSuperclass()));
        }
        return allFields;
    }
}