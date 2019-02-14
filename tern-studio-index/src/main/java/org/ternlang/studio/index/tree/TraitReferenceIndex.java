package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.SUPER;

import org.ternlang.core.Compilation;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.reference.TypeNavigation;
import org.ternlang.tree.reference.TypeReference;

public class TraitReferenceIndex implements Compilation {
   
   private final TypeReference reference;

   public TraitReferenceIndex(TypeNavigation root, TypeNavigation... list) {
      this.reference = new TypeReference(root, list);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Scope scope = module.getScope();
      Value value = reference.evaluate(scope, null);
      Object object = value.getValue();
      String type = String.valueOf(object);
      String prefix = module.getName();
      
      return new IndexResult(SUPER, reference, null, prefix, type, path, line);
   }
}
