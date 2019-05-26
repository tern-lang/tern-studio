package org.ternlang.studio.build;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.annotation.Component;
import org.ternlang.studio.common.display.DisplayResourceMatcher;

import lombok.SneakyThrows;

@Component
public class TypeScriptService  {
   
   private static final String SOURCE_FOLDER = "src/main/resources/resource/ts";
   private static final String[] SOURCE_PATTERNS = {
      "src/main/resources/resource/js/*.js"
   };
   private static final String[] OUTPUT_FOLDERS = {
      "src/main/resources/resource/js",
      "target/classes/resource/js"
   };

   private final TypeScriptCompiler compiler;
   private final List<String> outputDirs;
   private final List<String> sourceFiles;
   private final File typescriptDir;
   
   public TypeScriptService(TypeScriptCompiler compiler) {
      this.typescriptDir = new File(SOURCE_FOLDER);
      this.outputDirs = Arrays.asList(OUTPUT_FOLDERS);
      this.sourceFiles = Arrays.asList(SOURCE_PATTERNS);
      this.compiler = compiler;
   }

   @SneakyThrows
   public void process(Request request, Response response)  {
      if(typescriptDir.getAbsoluteFile().exists()) {
         for(String outputDir : outputDirs) {
            compiler.compile(typescriptDir.getCanonicalFile(), 
                             new File(outputDir).getCanonicalFile(), 
                             sourceFiles);
         }
      }
   }

}