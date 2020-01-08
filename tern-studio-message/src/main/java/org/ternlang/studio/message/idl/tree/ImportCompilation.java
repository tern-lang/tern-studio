package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.tree.Qualifier;

public class ImportCompilation implements Compilation {
   
   private final Qualifier qualifier;
   
   public ImportCompilation(Qualifier qualifier) {
      this.qualifier = qualifier;
   }
   
   @Override
   public Import compile(Module module, Path path, int line) throws Exception {
      return new Import(qualifier, path);
   }
}
