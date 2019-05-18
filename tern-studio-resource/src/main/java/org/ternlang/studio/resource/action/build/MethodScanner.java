package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Ignore;
import org.ternlang.studio.resource.action.annotation.Verb;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.Parameter;
import org.ternlang.studio.resource.action.extract.ParameterBuilder;

public class MethodScanner extends ConstructorScanner {

   public MethodScanner(DependencySystem source, List<Extractor> extractors) {
      super(source, extractors);
   }

   public MultiValueMap<String, MethodDispatcher> createDispatchers(Class<?> type) throws Exception {
      MultiValueMap<String, MethodDispatcher> dispatchers = new LinkedMultiValueMap<String, MethodDispatcher>();

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

                  dispatchers.add(pattern, dispatcher);
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
      Annotation[] annotations = method.getAnnotations();
      MethodHeader header = createHeader(method, annotations);
      ParameterBuilder extractor = createExtractor(method);
      
      if (!method.isAccessible()) {
         method.setAccessible(true);
      }
      return new MethodExecutor(matcher, header, extractor, validator, method);
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
      Class[] types = method.getParameterTypes();

      if (types.length > 0) {
         Parameter[] parameters = new Parameter[types.length];

         for (int i = 0; i < types.length; i++) {
            parameters[i] = createParameter(types[i], annotations[i], false);
         }
         return parameters;
      }
      return new Parameter[] {};
   }

   private ParameterBuilder createExtractor(Method method) throws Exception {
      Parameter[] parameters = createParameters(method);

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
