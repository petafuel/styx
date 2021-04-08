package net.petafuel.styx.api.filter.authentication.boundary;

import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NameBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAccessToken {
    /**
     * you can specify a xs2a service binding for a resource-class or method
     * No service specification means only token validity is checked without service binding
     *
     * @return array of allowed services
     */
    XS2ATokenType[] allowedServices() default {};
}
