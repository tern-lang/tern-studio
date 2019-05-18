package org.ternlang.studio.resource.action.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the path of a method or class that
 * represents a HTTP service. A service is something that handles a request an
 * responds with a result. It is an endpoint.
 * 
 * @author Niall Gallagher
 * 
 * @see org.ternlang.studio.resource.action.annotation.Intercept
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
   String value() default "/";
}
