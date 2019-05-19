package org.ternlang.studio.build;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.common.display.DisplayResourceMatcher;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;
import org.ternlang.studio.resource.action.annotation.Component;

@Component
@ResourcePath("/.*.js")
public class TypeScriptResource implements Resource {
   
   private static final String SOURCE_FOLDER = "src/main/resources/resource/ts";
   private static final String[] SOURCE_PATTERNS = {
      "src/main/resources/resource/js/*.js"
   };
   private static final String[] OUTPUT_FOLDERS = {
      "src/main/resources/resource/js",
      "target/classes/resource/js"
   };

   private final DisplayResourceMatcher matcher;
   private final TypeScriptCompiler compiler;
   private final List<String> outputDirs;
   private final List<String> sourceFiles;
   private final File typescriptDir;
   
   public TypeScriptResource(TypeScriptCompiler compiler, DisplayResourceMatcher matcher) {
      this.typescriptDir = new File(SOURCE_FOLDER);
      this.outputDirs = Arrays.asList(OUTPUT_FOLDERS);
      this.sourceFiles = Arrays.asList(SOURCE_PATTERNS);
      this.compiler = compiler;
      this.matcher = matcher;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      if(typescriptDir.getAbsoluteFile().exists()) {
         for(String outputDir : outputDirs) {
            compiler.compile(typescriptDir.getCanonicalFile(), 
                             new File(outputDir).getCanonicalFile(), 
                             sourceFiles);
         }
      }
      Resource resource = matcher.match(request, response);
      
      if(resource == null) {
         throw new IOException("Could not match " + request);
      }
      resource.handle(request, response);
   }

}