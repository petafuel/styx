package net.petafuel.styx.api.filter;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NameBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * This means the annotated endpoint will use the preAuthId Header to match a pre-auth id with an already existing
 * access token in order to add the Authorisation Header to the following XS2A Request
 */
public @interface AcceptsPreStepAuth {
}
