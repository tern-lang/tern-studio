package org.ternlang.studio.core.loader;

import static org.ternlang.core.Reserved.GRAMMAR_FILE;
import static org.ternlang.core.Reserved.IMPORT_FILE;
import static org.ternlang.core.Reserved.INSTRUCTION_FILE;
import static org.ternlang.studio.project.config.WorkspaceConfiguration.JAR_FILE;
import static org.ternlang.studio.project.config.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.core.ComponentListener;
import org.ternlang.studio.project.HomeDirectory;

@Component
public class SandBoxBuilder implements ComponentListener {
   
   private final JarFileBuilder builder;
   
   public SandBoxBuilder(ClassPathResourceLoader loader) {
      this.builder = new JarFileBuilder(loader);
   }
   
   public void onReady()  {
      File directory = HomeDirectory.getPath(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, JAR_FILE);

      try {
         builder.create(SandBoxLauncher.class)
                  .addResource(SandBoxClassLoader.class)
                  .addResource("/" + GRAMMAR_FILE)
                  .addResource("/" + IMPORT_FILE)
                  .addResource("/" + INSTRUCTION_FILE)
                  .saveFile(file);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + file, e);
      }
   }
}