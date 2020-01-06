package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Property;

public interface Constraint {
   
   default boolean isPrimitive(String token) {
      String[] types = {"byte", "short", "int", "long", "float", "double", "boolean", "char"};
      
      for(String type : types) {
         if(type.equals(token)) {
            return true;
         }
      }
      return false;
   }
   
   void define(Scope scope, Property property) throws Exception;
   void process(Scope scope, Property property) throws Exception;
}
