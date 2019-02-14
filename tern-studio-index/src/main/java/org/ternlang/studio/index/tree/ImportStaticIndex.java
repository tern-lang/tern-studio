package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.IMPORT;

import org.ternlang.core.Compilation;
import org.ternlang.core.NoStatement;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.Qualifier;

public class ImportStaticIndex implements Compilation {
   
   private final Qualifier qualifier;
   private final Statement statement;

   public ImportStaticIndex(Qualifier qualifier) {
      this.statement = new NoStatement();
      this.qualifier = qualifier;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = qualifier.getTarget();
      String fullName = qualifier.getQualifier();
      
      return new IndexResult(IMPORT, statement, null, fullName, name, path, line);
   }
}