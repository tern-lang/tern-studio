package org.ternlang.studio.common.resource;

import static java.util.Collections.EMPTY_MAP;
import static org.ternlang.studio.common.resource.ThreadModel.SYNCHRONOUS;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ternlang.common.LeastRecentlyUsedMap;

public class ThreadModelFilter implements ResourceFilter {
   
   private static final Logger LOG = LoggerFactory.getLogger(ThreadModelFilter.class);
   
   private final Map<String, ThreadModel> models;
   private final Map<String, ThreadModel> cache;
   private final ThreadModelLocal local;
   private final ThreadModel fallback;

   public ThreadModelFilter() {
      this(EMPTY_MAP);
   }
   
   public ThreadModelFilter(Map<String, ThreadModel> models) {
      this(models, SYNCHRONOUS);
   }
   
   public ThreadModelFilter(Map<String, ThreadModel> models, ThreadModel fallback) {
      this(models, fallback, 10000);
   }
   
   public ThreadModelFilter(Map<String, ThreadModel> models, ThreadModel fallback, int capacity) {
      this.cache = new LeastRecentlyUsedMap<String, ThreadModel>(capacity);
      this.local = new ThreadModelLocal();
      this.fallback = fallback;
      this.models = models;
   }   

   @Override
   public synchronized boolean before(Request request, Response response) {      
      Path path = request.getPath();
      String target = path.getPath();
      ThreadModel model = cache.get(target);
      AtomicBoolean close = local.get();      

      try {
         if (model == null) {
            model = match(request, target);
   
            if (model != null) {
               cache.put(target, model);
            }
         }      
         close.set(model.synchronous);
      } catch (Exception e) {
         LOG.info("Could not determine resource type", e);
      } 
      return false;
   }   

   @Override
   public synchronized boolean after(Request request, Response response) {
      AtomicBoolean close = local.get(); 
      
      try {      
         if(close.get()) {
            response.close();
         }
      } catch (IOException e) {
         LOG.info("Could not close response", e);
      } 
      return true;
   }
   
   private synchronized ThreadModel match(Request request, String target) {
      Set<String> mappings = models.keySet();

      for (String mapping : mappings) {
         ThreadModel type = models.get(mapping);

         if (target.matches(mapping)) {
            return type;
         }
      }
      return fallback;
   }
   
   private static class ThreadModelLocal extends ThreadLocal<AtomicBoolean> {
      
      private final boolean close;
      
      public ThreadModelLocal() {
         this(false);
      }
      
      public ThreadModelLocal(boolean close) {
         this.close = close;
      }
      
      @Override
      public AtomicBoolean initialValue() {
         return new AtomicBoolean(close);
      }      
   }
}