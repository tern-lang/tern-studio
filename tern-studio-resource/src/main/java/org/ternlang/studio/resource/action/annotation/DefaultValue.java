package org.ternlang.studio.resource.action.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used to specify a default value if one is not present. A default
 * value is typically used for an optional request parameter or cookie.
 * 
 * @author Niall Gallagher
 */
@Target({ ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
   String value();
}
