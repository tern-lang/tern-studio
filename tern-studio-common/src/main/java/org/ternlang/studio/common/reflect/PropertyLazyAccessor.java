package org.ternlang.studio.common.reflect;

import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;

public class PropertyLazyAccessor implements Accessor {
   
   private final Cache<Class, Accessor> cache;
   private final String path;
   private final Class type;   
   
   public PropertyLazyAccessor(String path, Class type) {
      this.cache = new LeastRecentlyUsedCache<Class, Accessor>();
      this.path = path;
      this.type = type;
   }

   @Override
   public Class getType() {
      return type;
   }   

   @Override
   public <T> T getValue(Object source) {      
      try {
         if(source != null) {
            Class type = source.getClass();        
            Accessor accessor = getAccessor(type);
         
            return accessor.getValue(source);
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not select path for '" + path + "'", e);
      }
      return null;
            
   }
   
   private Accessor getAccessor(Class type) throws Exception {
      Accessor accessor = cache.fetch(type);
      
      if(accessor == null) {
         accessor = new PropertyPathAccessor(path, type);
         cache.cache(type, accessor);
      }
      return accessor;
   }

}