package org.ternlang.studio.common.display;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.xml.core.Persister;
import org.ternlang.studio.common.FileDirectorySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DisplayPersister {
   
   private static final String DISPLAY_FILE = ".display";
   
   private final AtomicReference<DisplayFile> reference;
   private final FileDirectorySource workspace;
   private final Persister persister;

   public DisplayPersister(FileDirectorySource workspace) {
      this.reference = new AtomicReference<DisplayFile>();
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
               persister.write(definition, displayFile);
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