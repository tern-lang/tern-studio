package org.ternlang.studio.agent.debug;

public class PropertyNameParser {

   public String parse(String name) {
      int index = name.indexOf('@');
      
      if(index >= 0) {
         return name.substring(0, index);
      }
      return name;
   }
}
