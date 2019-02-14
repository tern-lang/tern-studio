package org.ternlang.studio.index.tree;

import static java.util.Collections.EMPTY_LIST;
import static org.ternlang.studio.index.IndexType.FUNCTION;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.constraint.Constraint;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.ModifierList;
import org.ternlang.tree.constraint.FunctionName;
import org.ternlang.tree.function.ParameterList;
import org.ternlang.tree.script.ScriptFunction;

public class ScriptFunctionIndex implements Compilation {
   
   private final ParameterList parameters;
   private final ScriptFunction function;
   private final FunctionName identifier;
   private final Constraint constraint;
   
   public ScriptFunctionIndex(ModifierList list, FunctionName identifier, ParameterList parameters, Statement body){
      this(list, identifier, parameters, null, body);
   }
   
   public ScriptFunctionIndex(ModifierList list, FunctionName identifier, ParameterList parameters, Constraint constraint, Statement body){
      this.function = new ScriptFunction(list, identifier, parameters, constraint, body);
      this.parameters = parameters;
      this.constraint = constraint;
      this.identifier = identifier;
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
      return new IndexResult(FUNCTION, function, type, prefix, name, path, line);
   }  
}
