package org.ternlang.studio.resource.template;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.springframework.stereotype.Component;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourceMatcher;

//@Component
public class TemplateMatcher implements ResourceMatcher {
   
   private final Map<String, Method> cache;
   private final TemplateEngine engine;
   private final Object value;
   
   public TemplateMatcher(TemplateEngine engine, Object value) {
      this.cache = new ConcurrentHashMap<String, Method>();
      this.engine = engine;
      this.value = value;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String[] segments = path.getSegments();
      
      if(cache.isEmpty()) {
         Class type = value.getClass();
         Method[] methods = type.getDeclaredMethods();
         
         for(Method method : methods) {
            Class returnType = method.getReturnType();
            
            if(returnType == TemplateResult.class) {
               Class[] parameterTypes = method.getParameterTypes();
               
               if(parameterTypes.length == 2) {
                  if(parameterTypes[0] == Request.class && parameterTypes[1] == Response.class) {
                     String name = method.getName();
                     cache.put(name, method);
                  }
               }
            }
            method.setAccessible(true);
         }
         
      }
      Method match = cache.get(segments[0]);
      
      if(match != null) {
         return new TemplateResource(engine);
      }
      return null;
   }
   
   private class TemplateResource implements Resource {
      
      private final TemplateEngine engine;
      
      public TemplateResource(TemplateEngine engine) {
         this.engine = engine;
      }

      @Override
      public void handle(Request request, Response response) throws Throwable {
         Path path = request.getPath();
         String[] segments = path.getSegments();
         Method method = cache.get(segments[1]);
         TemplateResult result = (TemplateResult)method.invoke(value, request, response);
         TemplateModel model = result.getModel();
         String template = result.getTemplate();
         PrintStream stream = response.getPrintStream();
         String text = engine.renderTemplate(model, template);
         
         response.setContentType("text/html");
         stream.print(text);
         stream.close();
      }

   }
}