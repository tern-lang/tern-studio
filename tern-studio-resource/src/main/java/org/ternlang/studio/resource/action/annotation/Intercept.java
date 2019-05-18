package org.ternlang.studio.resource.action.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the path of a method or class that
 * represents a HTTP interceptor. An interceptor is something that is executed
 * before a service, it acts much like a Servlet Filter would.
 * 
 * @author Niall Gallagher
 * 
 * @see org.ternlang.studio.resource.action.annotation.Path
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Intercept {
   String value() default "/";
}
