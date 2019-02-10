package tern.studio.index.tree;

import static tern.studio.index.IndexType.VARIABLE;

import tern.core.Compilation;
import tern.core.Evaluation;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.core.variable.Value;
import tern.core.constraint.Constraint;
import tern.studio.index.IndexResult;
import tern.tree.Declaration;
import tern.tree.literal.TextLiteral;

public class DeclarationIndex implements Compilation {
   
   private final TextLiteral identifier;
   private final Declaration declaration;
   private final Constraint constraint;
   
   public DeclarationIndex(TextLiteral identifier) {
      this(identifier, null, null);
   }
   
   public DeclarationIndex(TextLiteral identifier, Constraint constraint) {      
      this(identifier, constraint, null);
   }
   
   public DeclarationIndex(TextLiteral identifier, Evaluation value) {
      this(identifier, null, value);
   }
   
   public DeclarationIndex(TextLiteral identifier, Constraint constraint, Evaluation value) {
      this.declaration = new Declaration(identifier, constraint, value);
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
