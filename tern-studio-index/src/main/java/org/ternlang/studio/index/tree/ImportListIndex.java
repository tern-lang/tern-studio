package org.ternlang.studio.index.tree;

import org.ternlang.core.Compilation;
import org.ternlang.core.NoStatement;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.Qualifier;

import static org.ternlang.studio.index.IndexType.IMPORT;

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
