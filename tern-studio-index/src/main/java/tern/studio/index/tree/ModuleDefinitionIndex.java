package tern.studio.index.tree;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.variable.Value;
import tern.studio.index.IndexResult;
import tern.studio.index.IndexType;
import tern.tree.annotation.AnnotationList;
import tern.tree.define.ModuleDefinition;
import tern.tree.define.ModuleName;
import tern.tree.define.ModulePart;

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
