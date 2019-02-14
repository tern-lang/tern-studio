package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.TypePart;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.studio.index.IndexType;
import org.ternlang.tree.annotation.AnnotationList;
import org.ternlang.tree.constraint.TraitName;
import org.ternlang.tree.define.TraitDefinition;
import org.ternlang.tree.define.TypeHierarchy;

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
