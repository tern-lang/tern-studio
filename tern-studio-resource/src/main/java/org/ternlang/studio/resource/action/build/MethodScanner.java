package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Ignore;
import org.ternlang.studio.resource.action.annotation.Verb;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.Parameter;
import org.ternlang.studio.resource.action.extract.ParameterBuilder;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class MethodScanner extends ConstructorScanner {

   public MethodScanner(DependencySystem source, List<Extractor> extractors) {
      super(source, extractors);
   }

   public Multimap<String, MethodDispatcher> createDispatchers(Class<?> type) throws Exception {
      Multimap<String, MethodDispatcher> dispatchers = LinkedHashMultimap.create();

      if (type != null) {
         ComponentType componentType = ComponentType.resolveType(type);
         List<ComponentBuilder> builders = createBuilders(type);
         String typePath = componentType.extractPath(type);

         while (type != null) {
            Method[] methods = type.getDeclaredMethods();

            for (Method method : methods) {
               String methodPath = componentType.extractPath(method);

               if (methodPath != null) {
                  MethodMatcher matcher = createMatcher(method, typePath, methodPath);
                  MethodDispatcher dispatcher = createDispatcher(builders, method, matcher);
                  String pattern = matcher.pattern();

                  if(dispatcher == null) {
                     throw new IllegalStateException("Could not resolve for " + pattern + " on " + method);
                  }
                  dispatchers.put(pattern, dispatcher);
               }
            }
            type = type.getSuperclass();
         }
      }
      return dispatchers;
   }
   
   private MethodMatcher createMatcher(Method method, String typePath, String methodPath) throws Exception {
      Ignore ignore = method.getAnnotation(Ignore.class);
      Annotation[] annotations = method.getAnnotations();
      String methodName = method.getName();
      String parentPath = "/";
      String ignorePath = "";
      
      if (ignore != null) {
         ignorePath = ignore.value();
         formatter.formatPath(ignorePath);
      }
      if (typePath != null) {
         parentPath = formatter.formatPath(typePath);
      }
      if (!methodPath.equals("/")) {
         methodPath = formatter.formatPath(methodPath);  
      } else {
         methodPath = formatter.formatPath(methodName);
      }
      for(Annotation annotation : annotations) {
         Class<? extends Annotation> methodVerb = annotation.annotationType();
         
         if(methodVerb.isAnnotationPresent(Verb.class)) {
            return new MethodMatcher(methodVerb, ignorePath, parentPath, methodPath);
         }
      }
      return new MethodMatcher(GET.class, ignorePath, parentPath, methodPath);
 
   }

   private MethodDispatcher createDispatcher(List<ComponentBuilder> builders, Method method, MethodMatcher expression) throws Exception {
      MethodExecutor executor = createExecutor(method, expression);

      if (executor != null) {
         return new MethodDispatcher(builders, executor);
      }
      return null;
   }

   private MethodExecutor createExecutor(Method method, MethodMatcher matcher) throws Exception {
      ParameterBuilder extractor = createExtractor(method);
      
      if(extractor != null) {
         Annotation[] annotations = method.getAnnotations();
         MethodHeader header = createHeader(method, annotations);
         
         if (!method.isAccessible()) {
            method.setAccessible(true);
         }
         return new MethodExecutor(matcher, header, extractor, validator, method);
      }
      return null;
   }

   private MethodHeader createHeader(Method method, Annotation[] annotations) throws Exception {
      MethodHeader header = new MethodHeader();

      for (Annotation annotation : annotations) {
         header.extractHeader(annotation);
      }
      return header;
   }

   private Parameter[] createParameters(Method method) throws Exception {
      Annotation[][] annotations = method.getParameterAnnotations();
      Type[] types = method.getGenericParameterTypes();
      Class[] classes = method.getParameterTypes();

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

   private ParameterBuilder createExtractor(Method method) throws Exception {
      Parameter[] parameters = createParameters(method);
      Class parent = method.getDeclaringClass();

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
