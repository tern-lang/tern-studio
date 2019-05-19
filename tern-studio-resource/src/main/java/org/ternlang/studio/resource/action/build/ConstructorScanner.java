package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.extract.ComponentExtractor;
import org.ternlang.studio.resource.action.extract.DependencyExtractor;
import org.ternlang.studio.resource.action.extract.DependencyListExtractor;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.Parameter;
import org.ternlang.studio.resource.action.extract.ParameterBuilder;
import org.ternlang.studio.resource.action.extract.RejectExtractor;

public class ConstructorScanner extends Scanner {

   public ConstructorScanner(DependencySystem system, List<Extractor> extractors) {
      super(system, extractors);
   }

   public List<ComponentBuilder> createBuilders(Class type) throws Exception {
      List<ComponentBuilder> matches = new LinkedList<ComponentBuilder>();
   
      if(type != null) {
         List<ComponentBuilder> builders = cache.get(type);
      
         if (builders == null) {
            Constructor[] factories = type.getDeclaredConstructors();
   
            for (Constructor factory : factories) {
               ComponentBuilder builder = createBuilder(type, factory);
   
               if (builder != null) {
                  matches.add(builder);
               }
            }
            if(matches.isEmpty()) {
               throw new IllegalStateException("Could not construct " + type);
            }
            cache.put(type, matches);
            return matches;
         }
         return builders;
      }
      return matches;
   }

   protected Extractor createDefaultExtractor(Class parent, Parameter parameter) throws Exception {
      Class type = parameter.getType();
      Class entry = parameter.getEntry();
      
      if (!converter.accept(type)) {
         if (isComponent(parameter)) {
            List<ComponentBuilder> builders = createBuilders(type);
            
            if(!builders.isEmpty()) {
               return new ComponentExtractor(builders, type);
            }
            return null;
         }
         if(parameter.isList()) {
            return new DependencyListExtractor(system, entry);
         }   
         return new DependencyExtractor(system, type);
      }
      return null;
   }
   
   protected Class createDependency(Type type) throws Exception  {
      if(ParameterizedType.class.isInstance(type)) {
         ParameterizedType real = (ParameterizedType)type;
         Type[] types = real.getActualTypeArguments();
         
         if(types.length > 0) {
            return (Class)types[0];
         }
      }
      return null;
   }

   private ComponentBuilder createBuilder(Class type, Constructor factory) throws Exception {
      ParameterBuilder extractor = createExtractor(factory);
      
      if(extractor != null) {
         PropertyInjector injector = createInjector(type);
   
         if (!factory.isAccessible()) {
            factory.setAccessible(true);
         }
         return new ComponentBuilder(system, extractor, injector, factory);
      }
      return null;
   }

   private FieldSetter createSetter(Field field) throws Exception {
      Annotation[] list = field.getAnnotations();
      Class type = field.getType();
      Class parent = field.getDeclaringClass();
      Property property = createProperty(type, null, list);
      
      if(property.isInjectable()) {
         Extractor extractor = createExtractor(parent, property);
   
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
      Type[] types = factory.getGenericParameterTypes();
      Class[] classes = factory.getParameterTypes();

      if (types.length > 0) {
         Parameter[] parameters = new Parameter[types.length];

         for (int i = 0; i < types.length; i++) {
            Class entry = createDependency(types[i]);
            Class type = classes[i];
            
            parameters[i] = createParameter(type, entry, annotations[i], true);
         }
         return parameters;
      }
      return new Parameter[] {};
   }

   private ParameterBuilder createExtractor(Constructor factory) throws Exception {
      Parameter[] parameters = createParameters(factory);
      Class parent = factory.getDeclaringClass();

      if (parameters.length > 0) {
         Extractor[] extractors = new Extractor[parameters.length];

         for (int i = 0; i < parameters.length; i++) {
            extractors[i] = createExtractor(parent, parameters[i]);
            
            if(extractors[i] == null) {
               return null;
            }
         }
         return new ParameterBuilder(extractors, parameters);
      }
      return new ParameterBuilder();
   }
}
