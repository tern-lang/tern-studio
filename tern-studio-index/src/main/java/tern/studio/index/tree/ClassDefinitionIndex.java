package tern.studio.index.tree;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.TypePart;
import tern.studio.index.IndexResult;
import tern.tree.annotation.AnnotationList;
import tern.tree.constraint.ClassName;
import tern.tree.define.ClassDefinition;
import tern.tree.define.TypeHierarchy;
import tern.tree.define.TypeName;

import static tern.studio.index.IndexType.CLASS;

public class ClassDefinitionIndex implements Compilation {
   
   private final ClassDefinition definition;
   private final TypeName identifier;
   
   public ClassDefinitionIndex(AnnotationList annotations, ClassName name, TypeHierarchy hierarchy, TypePart... parts) {
      this.definition = new ClassDefinition(annotations, name, hierarchy, parts);
      this.identifier = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Statement statement = definition.compile(module, path, line);
      Scope scope = module.getScope();
      String name = identifier.getName(scope);
      String prefix = module.getName();
      
      return new IndexResult(CLASS, statement, null, prefix, name, path, line);
   }
}
