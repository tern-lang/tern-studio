package tern.studio.index.tree;

import static tern.studio.index.IndexType.COMPOUND;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.studio.index.IndexResult;
import tern.tree.CompoundStatement;

public class CompoundStatementIndex implements Compilation {

   private final CompoundStatement statement;
   
   public CompoundStatementIndex(Statement... statements) {
      this.statement = new CompoundStatement(statements);
   }
   
   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      Object result = statement.compile(module, path, line);
      String prefix = module.getName();
      
      return new IndexResult(COMPOUND, result, null, prefix, "{}", path, line); // N.B name of {} is important for ordering nodes
   }

}
