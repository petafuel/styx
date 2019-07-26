package net.petafuel.styx.utils.http;

import java.lang.reflect.Field;

public class XS2AHeaderParser
{
    public static void parse(Object annotated, XS2ARequest xs2aRequest)
    {
        try
        {
            mapFields(annotated, xs2aRequest);
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private static void mapFields(Object o, XS2ARequest xs2aRequest) throws IllegalAccessException
    {
        Class<?> c = o.getClass();
        String headerValue = "";
        for (Field field : c.getDeclaredFields())
        {
            field.setAccessible(true);
            if (field.isAnnotationPresent(XS2AHeader.class))
            {
                if (field.getAnnotation(XS2AHeader.class).nested() && field.get(o) != null)
                {
                    mapFields(field.get(o), xs2aRequest);
                }
                else
                {
                    try
                    {
                        headerValue = (String) field.get(o);
                        if (headerValue != null && !headerValue.isEmpty())
                        {
                            xs2aRequest.setHeader(field.getAnnotation(XS2AHeader.class).value(), headerValue);
                        }
                    } catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }


    }
}
