package org.ternlang.studio.agent.debug;

public class VariableNameEncoder {

   private static final String DEFAULT_TOKEN = "__DOT__";
   
   private final String token;
   
   public VariableNameEncoder() {
      this(DEFAULT_TOKEN);
   }
   
   public VariableNameEncoder(String token) {
      this.token = token;
   }
   
   public String encode(String name) {
      if(name != null) {
         return name.replace(".", token);
      }
      return name;
   }
   
   public String decode(String name) {
      if(name != null) {
         return name.replace(token, ".");
      }
      return name;
   }
}