package tern.studio.index.tree;

import tern.core.Compilation;
import tern.core.NoStatement;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.variable.Value;
import tern.studio.index.IndexResult;
import tern.tree.Qualifier;

import static tern.studio.index.IndexType.IMPORT;

public class ImportListIndex implements Compilation {

   private final Qualifier qualifier;
   private final Qualifier[] names;
   private final IndexResult[] results;
   private final Statement statement;

   public ImportListIndex(Qualifier qualifier, Qualifier... names) {
      this.statement = new NoStatement();
      this.results = new IndexResult[names.length];
      this.qualifier = qualifier;
      this.names = names;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String prefix = qualifier.getQualifier();

      for(int i = 0; i < names.length; i++) {
         String name = names[i].getQualifier();
         results[i] = new IndexResult(IMPORT, statement, null, prefix + "." + name, name, path, line);
      }
      return results;
   }
}
