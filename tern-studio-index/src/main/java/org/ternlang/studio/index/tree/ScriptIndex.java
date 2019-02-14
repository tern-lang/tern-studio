package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.SCRIPT;

import org.ternlang.core.Compilation;
import org.ternlang.core.Statement;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.script.Script;

public class ScriptIndex implements Compilation {

   private final Script script;
      
   public ScriptIndex(Statement... statements) {
      this.script = new Script(statements);
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      String name = path.getPath();
      String prefix = module.getName();
      
      return new IndexResult(SCRIPT, script, null, prefix, name, path, line);
   }
}
