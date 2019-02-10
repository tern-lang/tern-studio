package tern.studio.index.tree;

import static java.util.Collections.EMPTY_LIST;
import static tern.core.Reserved.TYPE_CONSTRUCTOR;
import static tern.studio.index.IndexType.CONSTRUCTOR;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.TypePart;
import tern.studio.index.IndexResult;
import tern.tree.ModifierList;
import tern.tree.annotation.AnnotationList;
import tern.tree.define.ClassConstructor;
import tern.tree.function.ParameterList;

public class ClassConstructorIndex implements Compilation {

   private final ClassConstructor constructor;
   private final ParameterList parameters;
   
   public ClassConstructorIndex(AnnotationList annotations, ModifierList modifiers, ParameterList parameters, Statement body){  
      this(annotations, modifiers, parameters, null, body);
   }  
   
   public ClassConstructorIndex(AnnotationList annotations, ModifierList modifiers, ParameterList parameters, TypePart part, Statement body){  
      this.constructor = new ClassConstructor(annotations, modifiers, parameters, part, body);
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

