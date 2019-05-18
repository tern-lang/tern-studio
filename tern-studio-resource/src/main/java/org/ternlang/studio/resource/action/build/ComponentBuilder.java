package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.extract.ParameterBuilder;
import org.ternlang.studio.resource.action.validate.Validator;

public class ComponentBuilder {

   private final ParameterBuilder extractor;
   private final PropertyInjector injector;
   private final Constructor factory;
   private final Validator validator;

   public ComponentBuilder(ParameterBuilder extractor, PropertyInjector injector, Validator validator, Constructor factory) {
      this.validator = validator;
      this.extractor = extractor;
      this.injector = injector;
      this.factory = factory;
   }

   public ComponentType type() throws Exception {
      Class type = factory.getDeclaringClass();
      Annotation[] annotations = type.getAnnotations();

      if (annotations.length > 0) {
         return ComponentType.resolveType(type);
      }
      return null;
   }

   public Object build(Context context) throws Exception {
      Object[] arguments = extractor.extract(context);
      Object instance = factory.newInstance(arguments);

      if (injector != null) {
         injector.inject(instance, context);
      }
      return instance;
   }

   public float score(Context context) throws Exception {
      if (injector.valid(context)) {
         return extractor.score(context);
      }
      return -1;
   }
}
