package tern.studio.index.tree;

import static java.util.Collections.EMPTY_LIST;
import static tern.core.Reserved.TYPE_CONSTRUCTOR;
import static tern.studio.index.IndexType.CONSTRUCTOR;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.studio.index.IndexResult;
import tern.tree.ModifierList;
import tern.tree.annotation.AnnotationList;
import tern.tree.define.ClassConstructor;
import tern.tree.define.EnumConstructor;
import tern.tree.function.ParameterList;

public class EnumConstructorIndex implements Compilation {

   private final ClassConstructor constructor;
   private final ParameterList parameters;
   
   public EnumConstructorIndex(AnnotationList annotations, ModifierList modifiers, ParameterList parameters, Statement body){  
      this.constructor = new EnumConstructor(annotations, modifiers, parameters, body);
      this.parameters = parameters;
   }
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      String prefix = module.getName();
      String name = TYPE_CONSTRUCTOR;
      
      if(parameters != null) {
         name = name + parameters.create(scope, EMPTY_LIST);
      }
      return new IndexResult(CONSTRUCTOR, constructor, null, prefix, name, path, line);
   }
}