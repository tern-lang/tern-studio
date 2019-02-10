package tern.studio.index.tree;

import static tern.studio.index.IndexType.SUPER;

import tern.core.Compilation;
import tern.core.Evaluation;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.studio.index.IndexResult;
import tern.tree.constraint.TraitConstraint;

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
