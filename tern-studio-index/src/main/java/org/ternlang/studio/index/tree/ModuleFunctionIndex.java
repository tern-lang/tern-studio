package org.ternlang.studio.index.tree;

import static java.util.Collections.EMPTY_LIST;
import static org.ternlang.studio.index.IndexType.MEMBER_FUNCTION;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.constraint.Constraint;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.ModifierList;
import org.ternlang.tree.annotation.AnnotationList;
import org.ternlang.tree.constraint.FunctionName;
import org.ternlang.tree.define.ModuleFunction;
import org.ternlang.tree.function.ParameterList;

public class ModuleFunctionIndex implements Compilation {
   
   private final ParameterList parameters;
   private final ModuleFunction function;
   private final FunctionName identifier;
   private final Constraint constraint;

   public ModuleFunctionIndex(AnnotationList annotations, ModifierList modifiers, FunctionName identifier, ParameterList parameters, Statement body){
      this(annotations, modifiers, identifier, parameters, null, body);
   }
   
   public ModuleFunctionIndex(AnnotationList annotations, ModifierList modifiers, FunctionName identifier, ParameterList parameters, Constraint constraint, Statement body){
      this.function = new ModuleFunction(annotations, modifiers, identifier, parameters, constraint, body);
      this.parameters = parameters;
      this.identifier = identifier;
      this.constraint = constraint;
   }
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
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
