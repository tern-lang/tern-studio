package org.ternlang.studio.service.loader;

public class ClassPathFilter {

   private final String[] prefixes;
   
   public ClassPathFilter(String[] prefixes) {
      this.prefixes = prefixes;
   }
   
   public boolean accept(String name) {
      if(name != null) {
         Class type = getClass();
         
         for(String prefix : prefixes) {
            if(name.startsWith(prefix)) {
               try {
                  Class.forName(name);
                  return true;
               } catch(Exception e) {
                  return type.getResource(name) != null;
               }
            }
         }
      }
      return false;
   }
}