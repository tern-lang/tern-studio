package org.ternlang.studio.agent.cli;

public class CommandValue {

   private final String name;
   private final Object value;

   public CommandValue(String name, Object value) {
      this.name = name;
      this.value = value;
   }
   
   public String getName(){
      return name;
   }
   
   public Object getValue(){
      return value;
   }
}
