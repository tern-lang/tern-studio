package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.Evaluation;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.variable.Value;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.reference.TypeNavigation;

public class TypeReferencePartIndex implements Compilation {

   private final NameReference reference;  
   
   public TypeReferencePartIndex(Evaluation type) {
      this.reference = new NameReference(type);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = reference.getName(scope);
      
      return new TypeIndexPart(name);
   }
   
   private static class TypeIndexPart extends TypeNavigation {
      
      private final String name;
      
      public TypeIndexPart(String name) {
         this.name = name;
      }

      @Override
      public String qualify(Scope scope, String left) throws Exception {
         if(left != null) {
            return left + '$' +name;
         }
         return name;
      }
      
      @Override
      public Value evaluate(Scope scope, Value left) {
         Object object = left.getValue();
         
         if(object != null) {
            return Value.getTransient(object + "." + name);
         }
         return Value.getTransient(name);
      }
   }
}