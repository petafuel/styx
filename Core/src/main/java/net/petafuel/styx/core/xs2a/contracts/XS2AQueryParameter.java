package net.petafuel.styx.core.xs2a.contracts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Inherited
public @interface XS2AQueryParameter {
    String value() default "";

    boolean nested() default false;
}