package tern.studio.index.tree;

import static java.util.Collections.EMPTY_LIST;
import static tern.studio.index.IndexType.FUNCTION;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.constraint.Constraint;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.studio.index.IndexResult;
import tern.tree.ModifierList;
import tern.tree.constraint.FunctionName;
import tern.tree.function.ParameterList;
import tern.tree.script.ScriptFunction;

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
