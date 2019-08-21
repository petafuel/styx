package net.petafuel.styx.core.xs2a.contracts;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Inherited
public @interface XS2AHeader {
    String value() default "";

    boolean nested() default false;
}