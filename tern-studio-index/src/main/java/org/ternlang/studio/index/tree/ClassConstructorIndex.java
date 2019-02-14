package org.ternlang.studio.index.tree;

import static java.util.Collections.EMPTY_LIST;
import static org.ternlang.core.Reserved.TYPE_CONSTRUCTOR;
import static org.ternlang.studio.index.IndexType.CONSTRUCTOR;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.TypePart;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.ModifierList;
import org.ternlang.tree.annotation.AnnotationList;
import org.ternlang.tree.define.ClassConstructor;
import org.ternlang.tree.function.ParameterList;

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

