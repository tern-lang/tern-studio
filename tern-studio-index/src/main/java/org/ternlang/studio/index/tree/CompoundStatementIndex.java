package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.COMPOUND;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.CompoundStatement;

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
