package tern.studio.index.tree;

import static tern.studio.index.IndexType.SCRIPT;

import tern.core.Compilation;
import tern.core.Statement;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.studio.index.IndexResult;
import tern.tree.script.Script;

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
