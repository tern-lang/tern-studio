package org.ternlang.studio.agent.core;

import org.ternlang.core.link.PackageLinker;
import org.ternlang.core.module.Path;
import org.ternlang.studio.agent.ProcessContext;

public class CompileValidator {
   
   private static final String SOURCE =
   "class InternalTypeForScriptAgent {\n"+
   "   static const ARR = [\"a\",\"b\",\"c\"];\n"+
   "   var x;\n"+
   "   new(index){\n"+
   "      this.x=ARR[index];\n"+
   "   }\n"+
   "   dump(){\n"+
   "      println(x);\n"+
   "   }\n"+
   "}\n"+
   "var privateVariableInScriptAgent = new InternalTypeForScriptAgent(1);\n"+
   "privateVariableInScriptAgent.dump();\n"+
   "println(privateVariableInScriptAgent.x);\n"+
   "println(InternalTypeForScriptAgent.ARR);";
   
   private final ProcessContext context;
   private final Path path;
   
   public CompileValidator(ProcessContext context) {
      this.path = new Path("/internalPrivateScript.tern");
      this.context = context;
   }

   public void validate() {
      PackageLinker linker = context.getLinker();
      
      try {
         linker.link(path, SOURCE, "script");
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}