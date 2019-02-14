package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.SUPER;

import org.ternlang.core.Compilation;
import org.ternlang.core.Evaluation;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.constraint.TraitConstraint;

public class TraitConstraintIndex implements Compilation {
   
   private final TraitConstraint constraint;

   public TraitConstraintIndex(Evaluation constraint) {
      this.constraint = new TraitConstraint(constraint);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Type type = constraint.getType(scope);
      String name = String.valueOf(type);
      String prefix = module.getName();
      
      return new IndexResult(SUPER, constraint, null, prefix, name, path, line);
   }
}
