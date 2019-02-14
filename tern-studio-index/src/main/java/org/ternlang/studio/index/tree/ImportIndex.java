package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.IMPORT;

import org.ternlang.core.Compilation;
import org.ternlang.core.Evaluation;
import org.ternlang.core.NoStatement;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.Qualifier;

public class ImportIndex implements Compilation {
   
   private final Qualifier qualifier;
   private final Statement statement;
   private final Evaluation alias;

   public ImportIndex(Qualifier qualifier) {
      this(qualifier, null);
   }
   
   public ImportIndex(Qualifier qualifier, Evaluation alias) {
      this.statement = new NoStatement();
      this.qualifier = qualifier;
      this.alias = alias;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = qualifier.getTarget();
      String fullName = qualifier.getQualifier();
      
      if(alias != null) {
         Scope scope = module.getScope();
         Value value = alias.evaluate(scope, null);
         
         name = value.getString();
      }
      return new IndexResult(IMPORT, statement, null, fullName, name, path, line);
   }
}
