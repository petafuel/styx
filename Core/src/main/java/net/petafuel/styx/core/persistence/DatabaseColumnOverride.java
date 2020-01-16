package net.petafuel.styx.core.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This holds a pair of the (original) column name from the model and a (replacement) column name from the resultset
 * which should map to the original
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseColumnOverride {
    /**
     * Original column name in the target model/class
     *
     * @return
     */
    String original();

    /**
     * Column name received in the result set
     *
     * @return
     */
    String replacement();
}
