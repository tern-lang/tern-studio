package org.ternlang.studio.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ScriptProcessor {

   private final ScriptCompiler compiler;

   public ScriptProcessor(ScriptCompiler compiler) {
      this.compiler = compiler;
   }
   
   public void process(File file) throws IOException  {
      File sourceDir = file.getParentFile();    
      
      if(!sourceDir.exists()) {
         throw new FileNotFoundException("Script file '" + file + "' could not be found");
      }
      String sourceName = file.getName();
      
      if(!sourceName.endsWith(".min.js")) {
         String compileName = sourceName.replace(".js", ".min.js");
         File outputFile = new File(sourceDir, compileName);
         ScriptResult result = compiler.compile(file);
         String message = result.getMessage();
         String source = result.getSource();
         
         if(!result.isSuccess()) {
            throw new IOException("Could not compile script '" + file + "' " + message);
         }
         OutputStream output = new FileOutputStream(outputFile);
         PrintStream writer = new PrintStream(output);
         
         writer.println(source);
         writer.close();
      }
   }
}