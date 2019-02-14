package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.TypePart;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.annotation.AnnotationList;
import org.ternlang.tree.constraint.ClassName;
import org.ternlang.tree.define.ClassDefinition;
import org.ternlang.tree.define.TypeHierarchy;
import org.ternlang.tree.define.TypeName;

import static org.ternlang.studio.index.IndexType.CLASS;

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
