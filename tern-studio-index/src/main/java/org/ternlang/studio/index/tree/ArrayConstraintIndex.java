package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.Evaluation;
import org.ternlang.core.constraint.Constraint;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.variable.Value;
import org.ternlang.parse.StringToken;

public class ArrayConstraintIndex implements Compilation {
   
   private static final String[] DIMENSIONS = {"", "[]", "[][]", "[][][]", "[][][][]" };

   private final IndexConstraint constraint;

   public ArrayConstraintIndex(Constraint entry, StringToken... name) {
      this(entry, DIMENSIONS[name.length], name.length, 0);
   }
   
   public ArrayConstraintIndex(Constraint entry, String name, int bounds) {
      this(entry, name, bounds, 0);
   }
   
   public ArrayConstraintIndex(Constraint entry, String name, int bounds, int modifiers) {
      this.constraint = new IndexConstraint(entry, bounds);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      return constraint;
   }
   
   private static class IndexConstraint extends Evaluation {
      
      private final Constraint entry;
      private final int bounds;
      
      public IndexConstraint(Constraint entry, int bounds) {
         this.bounds = bounds;
         this.entry = entry;
      }

      @Override
      public Value evaluate(Scope scope, Value left) throws Exception {
         String type = entry.getName(scope);
         String name = type + DIMENSIONS[bounds];
         
         return Value.getTransient(name);
      }
   }
}
