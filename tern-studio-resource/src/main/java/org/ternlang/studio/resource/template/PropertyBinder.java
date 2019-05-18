package org.ternlang.studio.resource.template;

import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;
import org.ternlang.studio.resource.template.reflect.Accessor;
import org.ternlang.studio.resource.template.reflect.PropertyPathAccessor;

public class PropertyBinder {
   
   private final Cache<Class, PropertyExtractor> extractors;
   private final int capacity;

   public PropertyBinder() {
      this(100);
   }
   
   public PropertyBinder(int capacity) {
      this.extractors = new LeastRecentlyUsedCache<Class, PropertyExtractor>(capacity);
      this.capacity = capacity;
   }
   
   public Object getValue(String property, Object source) {
      Class type = source.getClass(); 
      
      try {
         PropertyExtractor extractor = extractors.fetch(type);
         
         if(extractor == null) {
            extractor = new PropertyExtractor(type, capacity);
            extractors.cache(type, extractor);
         }
         return extractor.getValue(property, source);
      } catch(Exception e) {
         throw new RuntimeException("Could not accessor property '" + property + "' on " + type);
      }
   }

   
   private static class PropertyExtractor {
      
      private final Cache<String, Accessor> accessors;
      private final Class type;
      
      public PropertyExtractor(Class type, int capacity) {
         this.accessors = new LeastRecentlyUsedCache<String, Accessor>(capacity);
         this.type = type;
      }
      
      public Object getValue(String property, Object source) throws Exception {
         Accessor accessor = accessors.fetch(property);     
         
         if(accessor == null) {
            accessor = new PropertyPathAccessor(property, type);
            accessors.cache(property, accessor);
         }
         return accessor.getValue(source);
      }
               
   }
}