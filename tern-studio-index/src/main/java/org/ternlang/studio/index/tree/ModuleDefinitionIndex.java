package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.studio.index.IndexType;
import org.ternlang.tree.annotation.AnnotationList;
import org.ternlang.tree.define.ModuleDefinition;
import org.ternlang.tree.define.ModuleName;
import org.ternlang.tree.define.ModulePart;

public class ModuleDefinitionIndex implements Compilation {
   
   private final ModuleDefinition definition;
   private final ModuleName identifier;
   
   public ModuleDefinitionIndex(AnnotationList annotations, ModuleName module, ModulePart... body) {
      this.definition = new ModuleDefinition(annotations, module, body);
      this.identifier = module;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Statement statement = definition.compile(module, path, line);
      Scope scope = module.getScope();
      Value value = identifier.evaluate(scope, null);
      String name = value.getString();
      String prefix = module.getName();
      
      return new IndexResult(IndexType.MODULE, statement, null, prefix, name, path, line);
   }
}
