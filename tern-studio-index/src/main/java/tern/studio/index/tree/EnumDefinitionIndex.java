package tern.studio.index.tree;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.TypePart;
import tern.studio.index.IndexResult;
import tern.tree.annotation.AnnotationList;
import tern.tree.constraint.EnumName;
import tern.tree.define.EnumDefinition;
import tern.tree.define.EnumList;
import tern.tree.define.TypeHierarchy;
import tern.tree.define.TypeName;

import static tern.studio.index.IndexType.ENUM;

public class EnumDefinitionIndex implements Compilation {
   
   private final EnumDefinition definition;
   private final TypeName identifier;
   
   public EnumDefinitionIndex(AnnotationList annotations, EnumName name, TypeHierarchy hierarchy, EnumList list, TypePart... parts) {
      this.definition = new EnumDefinition(annotations, name, hierarchy, list, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Statement statement = definition.compile(module, path, line);
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      
      return new IndexResult(ENUM, statement, null, prefix, name, path, line);
   }
}
