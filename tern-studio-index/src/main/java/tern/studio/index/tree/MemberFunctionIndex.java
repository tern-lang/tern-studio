package tern.studio.index.tree;

import static java.util.Collections.EMPTY_LIST;
import static tern.studio.index.IndexType.MEMBER_FUNCTION;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.constraint.Constraint;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.studio.index.IndexResult;
import tern.tree.ModifierList;
import tern.tree.annotation.AnnotationList;
import tern.tree.constraint.FunctionName;
import tern.tree.define.MemberFunction;
import tern.tree.function.ParameterList;
public class MemberFunctionIndex implements Compilation {
   
   private final ParameterList parameters;
   private final MemberFunction function;
   private final FunctionName identifier;
   private final Constraint constraint;
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, FunctionName identifier, ParameterList parameters){
      this(annotations, modifiers, identifier, parameters, null, null);
   }
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, FunctionName identifier, ParameterList parameters, Constraint constraint){
      this(annotations, modifiers, identifier, parameters, constraint, null);
   }
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, FunctionName identifier, ParameterList parameters, Statement body){
      this(annotations, modifiers, identifier, parameters, null, body);
   }
   
   public MemberFunctionIndex(AnnotationList annotations, ModifierList modifiers, FunctionName identifier, ParameterList parameters, Constraint constraint, Statement body){
      this.function = new MemberFunction(annotations, modifiers, identifier, parameters, constraint, body);
      this.constraint = constraint;
      this.identifier = identifier;
      this.parameters = parameters;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName(); // this is not the correct package
      String type = null;
      
      if(parameters != null) {
         name = name + parameters.create(scope, EMPTY_LIST);
      }
      if(constraint != null) {
         Type object = constraint.getType(scope);
         type = String.valueOf(object);
      }
      return new IndexResult(MEMBER_FUNCTION, function, type, prefix, name, path, line);
   }
}
