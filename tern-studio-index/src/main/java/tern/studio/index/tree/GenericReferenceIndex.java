package tern.studio.index.tree;

import tern.core.Compilation;
import tern.core.Evaluation;
import tern.core.IdentityEvaluation;
import tern.core.constraint.Constraint;
import tern.core.error.InternalStateException;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.core.type.index.ScopeType;
import tern.core.variable.Value;
import tern.parse.StringToken;
import tern.tree.literal.TextLiteral;
import tern.tree.reference.GenericArgumentList;

import static tern.core.ModifierType.CLASS;
import static tern.core.variable.Value.NULL;

public class GenericReferenceIndex implements Compilation {
   
   private final GenericArgumentList list;
   private final Evaluation evaluation;

   public GenericReferenceIndex(StringToken token) {
      this(new TextLiteral(token));
   }

   public GenericReferenceIndex(Evaluation evaluation) {
      this(evaluation, null);
   }

   public GenericReferenceIndex(Evaluation evaluation, GenericArgumentList list) {
      this.evaluation = evaluation;
      this.list = list;
   }

   @Override
   public Evaluation compile(Module module, Path path, int line) throws Exception {
      Constraint constraint = new IndexConstraint(evaluation);
      return new IdentityEvaluation(constraint, constraint);
   }   
   
   private static class IndexConstraint extends Constraint {
      
      private final Evaluation reference;
      
      public IndexConstraint(Evaluation reference) {
         this.reference = reference;
      }

      @Override
      public Type getType(Scope scope) {
         try {
            Value value = reference.evaluate(scope, NULL);
            Module module = scope.getModule();
            String entry = value.getValue();

            return new IndexType(module, entry);
         } catch(Exception e) {
            throw new InternalStateException("Invalid array constraint", e);
         }
      }
   }
   
   private static class IndexType extends ScopeType {

      public IndexType(Module module, String name) {
         super(module,null, name, CLASS.mask, 0);
      }
      
      @Override
      public String toString() {
         return getName();
      }
      
   }

}
