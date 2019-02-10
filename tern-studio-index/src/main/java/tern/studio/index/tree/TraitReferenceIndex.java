package tern.studio.index.tree;

import static tern.studio.index.IndexType.SUPER;

import tern.core.Compilation;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.variable.Value;
import tern.studio.index.IndexResult;
import tern.tree.reference.TypeNavigation;
import tern.tree.reference.TypeReference;

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
