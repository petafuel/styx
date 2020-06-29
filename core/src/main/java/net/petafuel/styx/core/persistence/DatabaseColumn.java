package net.petafuel.styx.core.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a single cell with a column name. Can be used so StyxifySQL can map a result set to a model
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseColumn {
    /**
     * ResultSet column name
     *
     * @return
     */
    String value();

    /**
     * Will reflect the specified type and recursivly fill the sub class
     *
     * @return
     */
    boolean nested() default false;

    /**
     * An array of target model/class column names that should be mapped to custom replacement column names from the result set
     *
     * @return
     */
    DatabaseColumnOverride[] overrides() default {};
}
