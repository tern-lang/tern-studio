package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.ternlang.studio.resource.action.annotation.AttributeParam;
import org.ternlang.studio.resource.action.annotation.CookieParam;
import org.ternlang.studio.resource.action.annotation.HeaderParam;
import org.ternlang.studio.resource.action.annotation.Inject;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.QueryParam;
import org.ternlang.studio.resource.action.extract.Parameter;

public class Property extends Parameter {

   public Property(Class type, Class entry, String value, Map<Class, Annotation> annotations) {
      this(type, entry, value, annotations, false);
   }

   public Property(Class type, Class entry, String value, Map<Class, Annotation> annotations, boolean required) {
      super(type, entry, value, annotations, required);
   }
   
   public boolean isInjectable() {
      return getAnnotation(Inject.class) != null ||
            getAnnotation(PathParam.class) != null ||
            getAnnotation(QueryParam.class) != null ||
            getAnnotation(CookieParam.class) != null ||
            getAnnotation(HeaderParam.class) != null ||
            getAnnotation(AttributeParam.class) != null;
   }
}
