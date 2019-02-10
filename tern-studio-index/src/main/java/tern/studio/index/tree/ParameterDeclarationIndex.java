package tern.studio.index.tree;

import static tern.core.constraint.Constraint.NONE;
import static tern.studio.index.IndexType.PARAMETER;

import tern.core.Compilation;
import tern.core.Evaluation;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.core.variable.Value;
import tern.core.constraint.Constraint;
import tern.core.function.Parameter;
import tern.studio.index.IndexResult;
import tern.tree.Modifier;
import tern.tree.ModifierList;
import tern.tree.NameReference;
import tern.tree.annotation.AnnotationList;
import tern.tree.function.ParameterDeclaration;

public class ParameterDeclarationIndex implements Compilation  {
   
   private final ParameterDeclaration declaration;
   private final Evaluation identifier;
   private final Constraint constraint;
   
   public ParameterDeclarationIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier){
      this(annotations, modifiers, identifier, null, null);
   }
   
   public ParameterDeclarationIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, Constraint constraint){
      this(annotations, modifiers, identifier, null, constraint);
   }
   
   public ParameterDeclarationIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, Modifier modifier){
      this(annotations, modifiers, identifier, modifier, null);
   }
   
   public ParameterDeclarationIndex(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, Modifier modifier, Constraint constraint){
      this.declaration = new IndexParameterDeclaration(annotations, modifiers, identifier, modifier, constraint);
      this.identifier = identifier;
      this.constraint = constraint;
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
      return new IndexResult(PARAMETER, declaration, type, prefix, name, path, line);
   }
   
   private static class IndexParameterDeclaration extends ParameterDeclaration {
      
      private final NameReference reference;
      private final Modifier modifier;
      
      public IndexParameterDeclaration(AnnotationList annotations, ModifierList modifiers, Evaluation identifier, Modifier modifier, Constraint constraint){
         super(annotations, modifiers, identifier, modifier, constraint);
         this.reference = new NameReference(identifier);
         this.modifier = modifier;
      }

      @Override
      public Parameter get(Scope scope, int index) throws Exception {
         String name = reference.getName(scope);
         return new Parameter(name, NONE, index, modifier != null);
      }
   }
}