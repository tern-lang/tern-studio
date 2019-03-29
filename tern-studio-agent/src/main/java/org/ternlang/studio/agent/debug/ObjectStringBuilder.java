package org.ternlang.studio.agent.debug;

public class ObjectStringBuilder {

   public static String toString(Object value) {
      try {
         return String.valueOf(value);
      } catch(Throwable e) {
         return "";
      }
   }
}
