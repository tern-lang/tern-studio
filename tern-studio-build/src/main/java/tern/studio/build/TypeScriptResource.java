package tern.studio.build;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.common.resource.display.DisplayResourceMatcher;
import org.springframework.stereotype.Component;

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