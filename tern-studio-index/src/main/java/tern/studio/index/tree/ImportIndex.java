package tern.studio.index.tree;

import static tern.studio.index.IndexType.IMPORT;

import tern.core.Compilation;
import tern.core.Evaluation;
import tern.core.NoStatement;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.variable.Value;
import tern.studio.index.IndexResult;
import tern.tree.Qualifier;

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
