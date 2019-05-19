package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.extract.ParameterBuilder;

public class ComponentBuilder {

   private final ParameterBuilder builder;
   private final PropertyInjector injector;
   private final DependencySystem system;
   private final Constructor factory;

   public ComponentBuilder(DependencySystem system, ParameterBuilder builder, PropertyInjector injector, Constructor factory) {
      this.builder = builder;
      this.injector = injector;
      this.factory = factory;
      this.system = system;
   }
   
   public Class[] require() throws Exception {
      return builder.require();
   }

   public ComponentType type() throws Exception {
      Class type = factory.getDeclaringClass();
      Annotation[] annotations = type.getAnnotations();

      if (annotations.length > 0) {
         return ComponentType.resolveType(type);
      }
      return null;
   }

   public <T> T build(Context context) throws Exception {
      Object instance = resolve(context);

      if (injector != null) {
         injector.inject(instance, context);
      }
      return (T)instance;
   }
   
   public <T> T resolve(Context context) throws Exception {
      Class type = factory.getDeclaringClass();
      Object instance = system.resolve(type);
      
      if(instance == null) {
         Object[] arguments = builder.extract(context);
         Object object = factory.newInstance(arguments);
   
         if (object != null) {
            system.register(object);
         }
         return (T)object;
      }
      return (T)instance;
   }

   public float score(Context context) throws Exception {
      if (injector.valid(context)) {
         return builder.score(context);
      }
      return -1;
   }
}
