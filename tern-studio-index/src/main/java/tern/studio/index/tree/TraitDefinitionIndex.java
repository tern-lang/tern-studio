package tern.studio.index.tree;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.TypePart;
import tern.studio.index.IndexResult;
import tern.studio.index.IndexType;
import tern.tree.annotation.AnnotationList;
import tern.tree.constraint.TraitName;
import tern.tree.define.TraitDefinition;
import tern.tree.define.TypeHierarchy;

public class TraitDefinitionIndex implements Compilation {
   
   private final TraitDefinition definition;
   private final TraitName identifier;
   
   public TraitDefinitionIndex(AnnotationList annotations, TraitName name, TypeHierarchy hierarchy, TypePart... parts) {
      this.definition = new TraitDefinition(annotations, name, hierarchy, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Statement statement = definition.compile(module, path, line);
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      
      return new IndexResult(IndexType.TRAIT, statement, null, prefix, name, path, line);
   }
}
