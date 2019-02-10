package tern.studio.service.loader;

import static tern.core.Reserved.GRAMMAR_FILE;
import static tern.core.Reserved.IMPORT_FILE;
import static tern.core.Reserved.INSTRUCTION_FILE;
import static tern.studio.project.config.WorkspaceConfiguration.JAR_FILE;
import static tern.studio.project.config.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;

import javax.annotation.PostConstruct;

import tern.studio.project.HomeDirectory;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
public class SandBoxBuilder {
   
   private final JarFileBuilder builder;
   private final Workspace workspace;
   
   public SandBoxBuilder(ClassPathResourceLoader loader, Workspace workspace) {
      this.builder = new JarFileBuilder(loader);
      this.workspace = workspace;
   }
   
   @PostConstruct
   public void create() throws Exception {
      File directory = HomeDirectory.getPath(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, JAR_FILE);

      builder.create(SandBoxLauncher.class)
               .addResource(SandBoxClassLoader.class)
               .addResource("/" + GRAMMAR_FILE)
               .addResource("/" + IMPORT_FILE)
               .addResource("/" + INSTRUCTION_FILE)
               .saveFile(file);
   }
}