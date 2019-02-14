package org.ternlang.studio.agent.runtime;

public class EmptyValue implements RuntimeValue {

   public final String name;

   public EmptyValue() {
      this(null);
   }

   public EmptyValue(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getValue() {
      return null;
   }
}
