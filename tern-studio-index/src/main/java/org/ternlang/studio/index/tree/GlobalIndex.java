package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.VARIABLE;

import org.ternlang.core.Compilation;
import org.ternlang.core.Evaluation;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;
import org.ternlang.core.variable.Value;
import org.ternlang.core.constraint.Constraint;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.Declaration;
import org.ternlang.tree.Global;
import org.ternlang.tree.literal.TextLiteral;

public class GlobalIndex implements Compilation {

   private final TextLiteral identifier;
   private final Declaration declaration;
   private final Constraint constraint;

   public GlobalIndex(TextLiteral identifier) {
      this(identifier, null, null);
   }

   public GlobalIndex(TextLiteral identifier, Constraint constraint) {
      this(identifier, constraint, null);
   }

   public GlobalIndex(TextLiteral identifier, Evaluation value) {
      this(identifier, null, value);
   }

   public GlobalIndex(TextLiteral identifier, Constraint constraint, Evaluation value) {
      this.declaration = new Global(identifier, constraint, value);
      this.constraint = constraint;
      this.identifier = identifier;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      String prefix = module.getName();
      String type = null;

      if(constraint != null) {
         Type object = constraint.getType(scope);
         type = String.valueOf(object);
      }
      return new IndexResult(VARIABLE, declaration, type, prefix, name, path, line);
   }
}
