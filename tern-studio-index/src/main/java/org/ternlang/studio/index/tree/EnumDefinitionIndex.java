package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.TypePart;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.annotation.AnnotationList;
import org.ternlang.tree.constraint.EnumName;
import org.ternlang.tree.define.EnumDefinition;
import org.ternlang.tree.define.EnumList;
import org.ternlang.tree.define.TypeHierarchy;
import org.ternlang.tree.define.TypeName;

import static org.ternlang.studio.index.IndexType.ENUM;

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
