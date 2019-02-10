package tern.studio.index.tree;

import static tern.studio.index.IndexType.IMPORT;

import tern.core.Compilation;
import tern.core.NoStatement;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.studio.index.IndexResult;
import tern.tree.Qualifier;

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