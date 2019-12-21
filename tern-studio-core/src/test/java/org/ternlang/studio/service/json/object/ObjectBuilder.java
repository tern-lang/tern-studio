package org.ternlang.studio.service.json.object;

import java.lang.reflect.Constructor;

import org.ternlang.studio.service.json.document.TextTrie;

public class ObjectBuilder {
   
   private final TextTrie<Constructor<?>> index;
   
   public ObjectBuilder() {
      this.index = new TextTrie<Constructor<?>>();
   }
   
   public void index(Class<?> type) {
      try {
         if(!type.isArray()) {
            String name = type.getSimpleName();
            Constructor<?> factory = type.getDeclaredConstructor();
            
            factory.setAccessible(true);
            index.index(factory, name);
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not index " + type, e);
      }
   }

   public Object create(CharSequence name) {
      Constructor<?> factory = index.match(name);
      
      if(factory == null) {
         throw new IllegalArgumentException("Could not find type " + name);
      }
      try {
         return factory.newInstance();
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + name, e);
      }
   }
}
