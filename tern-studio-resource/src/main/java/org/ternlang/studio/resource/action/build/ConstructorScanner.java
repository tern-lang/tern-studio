package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.extract.ComponentExtractor;
import org.ternlang.studio.resource.action.extract.ComponentSourceExtractor;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.Parameter;
import org.ternlang.studio.resource.action.extract.ParameterBuilder;

public class ConstructorScanner extends Scanner {

   public ConstructorScanner(DependencySystem source, List<Extractor> extractors) {
      super(source, extractors);
   }

   public List<ComponentBuilder> createBuilders(Class type) throws Exception {
      List<ComponentBuilder> builders = new LinkedList<ComponentBuilder>();

      if (type != null) {
         Constructor[] factories = type.getDeclaredConstructors();

         for (Constructor factory : factories) {
            ComponentBuilder builder = createBuilder(type, factory);

            if (builder != null) {
               builders.add(builder);
            }
         }
      }
      return builders;
   }

   protected Extractor createDefaultExtractor(Parameter parameter) throws Exception {
      Class type = parameter.getType();

      if (isComponent(parameter)) {
         List<ComponentBuilder> builders = createBuilders(type);

         return new ComponentExtractor(builders, type);
      }
      return new ComponentSourceExtractor(system, type);
   }

   private ComponentBuilder createBuilder(Class type, Constructor factory) throws Exception {
      ParameterBuilder extractor = createExtractor(factory);
      PropertyInjector injector = createInjector(type);

      if (!factory.isAccessible()) {
         factory.setAccessible(true);
      }
      return new ComponentBuilder(extractor, injector, validator, factory);
   }

   private FieldSetter createSetter(Field field) throws Exception {
      Annotation[] list = field.getAnnotations();
      Class type = field.getType();
      Property property = createProperty(type, list);
      
      if(property.isInjectable()) {
         Extractor extractor = createExtractor(property);
   
         if (!field.isAccessible()) {
            field.setAccessible(true);
         }
         return new FieldSetter(property, extractor, field);
      }
      return null;
   }

   public PropertyInjector createInjector(Class type) throws Exception {
      List<FieldSetter> setters = new LinkedList<FieldSetter>();

      while (type != null) {
         Field[] fields = type.getDeclaredFields();

         for (Field field : fields) {
            FieldSetter setter = createSetter(field);

            if (setter != null) {
               setters.add(setter);
            }
         }
         type = type.getSuperclass();
      }
      return new PropertyInjector(setters, validator);
   }

   private Parameter[] createParameters(Constructor factory) throws Exception {
      Annotation[][] annotations = factory.getParameterAnnotations();
      Class[] types = factory.getParameterTypes();

      if (types.length > 0) {
         Parameter[] parameters = new Parameter[types.length];

         for (int i = 0; i < types.length; i++) {
            parameters[i] = createParameter(types[i], annotations[i], true);
         }
         return parameters;
      }
      return new Parameter[] {};
   }

   private ParameterBuilder createExtractor(Constructor factory) throws Exception {
      Parameter[] parameters = createParameters(factory);

      if (parameters.length > 0) {
         Extractor[] extractors = new Extractor[parameters.length];

         for (int i = 0; i < parameters.length; i++) {
            extractors[i] = createExtractor(parameters[i]);
         }
         return new ParameterBuilder(extractors, parameters);
      }
      return new ParameterBuilder();
   }
}
