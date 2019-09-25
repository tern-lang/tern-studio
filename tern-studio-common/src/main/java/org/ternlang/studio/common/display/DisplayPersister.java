package org.ternlang.studio.common.display;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.xml.core.Persister;
import org.ternlang.core.type.extend.FileExtension;
import org.ternlang.studio.common.FileDirectorySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DisplayPersister {
   
   private static final String DISPLAY_FILE = ".display";
   
   private final AtomicReference<DisplayFile> reference;
   private final FileDirectorySource workspace;
   private final FileExtension extension;
   private final Persister persister;

   public DisplayPersister(FileDirectorySource workspace) {
      this.reference = new AtomicReference<DisplayFile>();
      this.extension = new FileExtension();
      this.persister = new Persister();
      this.workspace = workspace;
   }
   
   public synchronized DisplayDefinition readDefinition(){
      return getDisplayFile().readDefinition();
   }
   
   public synchronized void saveDefinition(DisplayDefinition definition) {
      getDisplayFile().saveDefinition(definition);
   }
   
   private synchronized DisplayFile getDisplayFile() {
      DisplayFile displayFile = reference.get();
      
      if(displayFile == null) {
         File file = workspace.createFile(DISPLAY_FILE);
         displayFile = new DisplayFile(file);
         reference.set(displayFile);
      }
      return displayFile;
   }

   private class DisplayFile {
      
      private AtomicReference<DisplayDefinition> reference;
      private File displayFile;
      private long loadTime;
      
      public DisplayFile(File displayFile) {
         this.reference = new AtomicReference<DisplayDefinition>();
         this.displayFile = displayFile;
      }
      
      public void saveDefinition(DisplayDefinition definition) {
         try {
            if(displayFile.exists()) {
               StringWriter writer = new StringWriter();
               persister.write(definition, writer);
               String text = writer.toString();
               extension.writeText(displayFile, text, "UTF-8");
               loadTime = displayFile.lastModified();
            }
         }catch(Exception e) {
            log.info("Could not save display", e);
         }
         reference.set(definition);
      }
      
      public DisplayDefinition readDefinition() {            
         DisplayDefinition definition = reference.get();
      
         try {
            if(displayFile.exists()) {
               long modifiedTime = displayFile.lastModified();
               
               if(definition == null || loadTime < modifiedTime) {   
                  definition = persister.read(DisplayDefinition.class, displayFile);
                  loadTime = modifiedTime;
                  reference.set(definition);
               }
            }
         }catch(Exception e) {
            log.info("Could not read theme", e);
         }
         if(definition == null) {
            return DisplayDefinition.getDefault();
         }
         return definition;
      }
   }
}