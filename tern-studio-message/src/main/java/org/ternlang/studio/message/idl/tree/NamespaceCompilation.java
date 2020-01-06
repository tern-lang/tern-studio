package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.tree.Qualifier;

public class NamespaceCompilation implements Compilation {

   private final Qualifier qualifier;
   
   public NamespaceCompilation(Qualifier qualifier) {
      this.qualifier = qualifier;
   }
   
   @Override
   public Namespace compile(Module module, Path path, int line) throws Exception {
      return new Namespace(qualifier, path);
   }

}
