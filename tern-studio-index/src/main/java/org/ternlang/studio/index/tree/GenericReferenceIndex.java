package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.Evaluation;
import org.ternlang.core.IdentityEvaluation;
import org.ternlang.core.constraint.Constraint;
import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;
import org.ternlang.core.type.index.ScopeType;
import org.ternlang.core.variable.Value;
import org.ternlang.parse.StringToken;
import org.ternlang.tree.literal.TextLiteral;
import org.ternlang.tree.reference.GenericArgumentList;

import static org.ternlang.core.ModifierType.CLASS;
import static org.ternlang.core.variable.Value.NULL;

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
