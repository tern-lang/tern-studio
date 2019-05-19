package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ternlang.studio.resource.action.annotation.DefaultValue;
import org.ternlang.studio.resource.action.annotation.Inject;
import org.ternlang.studio.resource.action.annotation.Required;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.Parameter;
import org.ternlang.studio.resource.action.extract.StringConverter;
import org.ternlang.studio.resource.action.validate.AnnotationValidator;
import org.ternlang.studio.resource.action.validate.Validator;

public abstract class Scanner {

   protected final Map<Class, List<ComponentBuilder>> cache;
   protected final List<Extractor> extractors;
   protected final StringConverter converter;
   protected final DependencySystem system;
   protected final PathFormatter formatter;
   protected final Validator validator;

   protected Scanner(DependencySystem system, List<Extractor> extractors) {
      this(system, extractors, new AnnotationValidator());
   }

   protected Scanner(DependencySystem system, List<Extractor> extractors, Validator validator) {
      this.cache = new ConcurrentHashMap<Class, List<ComponentBuilder>>();
      this.converter = new StringConverter();
      this.formatter = new PathFormatter();
      this.extractors = extractors;
      this.validator = validator;
      this.system = system;
   }

   protected boolean isDependency(Parameter parameter) {
      try {
         Inject annotation = parameter.getAnnotation(Inject.class);
         boolean constructor = parameter.isConstructor();
         Class type = parameter.getType();

         if(annotation != null) {
            String name = annotation.value();
            return isDependency(type, name);
         }
         if(constructor) {
            return isDependency(type, null);
         }
         return false;
      } catch (Exception e) {
         return false;
      }
   }
   
   protected boolean isDependency(Class type, String name) {
      try {
         if(name != null) {
            int length = name.length();
            
            if(length > 0) {
               return system.resolve(type, name) != null;
            }
         }
         return system.resolve(type) != null;
      } catch (Exception e) {
         return false;
      } 
   }

   protected boolean isComponent(Parameter parameter) {
      Class type = parameter.getType();
      ComponentType component = ComponentType.resolveType(type);
      int modifiers = type.getModifiers();

      if (component != null) {
         if (Modifier.isAbstract(modifiers)) {
            return false;
         }
         if (Modifier.isInterface(modifiers)) {
            return false;
         }
         return true;
      }
      return false;
   }

   protected AnnotationContext createData(Annotation[] annotations) throws Exception {
      AnnotationContext data = new AnnotationContext();
      
      for (Annotation annotation : annotations) {
         if (annotation instanceof Required) {
            data.setRequired(true);
         } else if (annotation instanceof DefaultValue) {
            DefaultValue value = (DefaultValue) annotation;
            String text = value.value();

            data.setDefault(text);
         } 
         data.addAnnotation(annotation);
      }
      return data;
   }

   protected Property createProperty(Class type, Class entry, Annotation[] annotations) throws Exception {
      AnnotationContext data = createData(annotations);
      Map<Class, Annotation> map = data.getAnnotations();
      String value = data.getDefault();
      boolean required = data.isRequired();

      return new Property(type, entry, value, map, required);
   }

   protected Parameter createParameter(Class type, Class entry, Annotation[] annotations, boolean constructor) throws Exception {
      AnnotationContext data = createData(annotations);
      Map<Class, Annotation> map = data.getAnnotations();
      String value = data.getDefault();
      boolean required = data.isRequired();

      return new Parameter(type, entry, value, map, constructor, required);
   }

   protected Extractor createExtractor(Class type, Parameter parameter) throws Exception {
      for (Extractor extractor : extractors) {
         if (extractor.accept(parameter)) {
            return extractor;
         }
      }
      return createDefaultExtractor(type, parameter);
   }

   protected abstract Extractor createDefaultExtractor(Class type, Parameter parameter) throws Exception;

}