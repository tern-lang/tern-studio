package org.ternlang.studio.common.display;

import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.xml.core.Persister;
import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;
import org.ternlang.core.type.extend.FileExtension;
import org.ternlang.studio.common.FileDirectorySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DisplayPersister {
   
   private static final String DISPLAY_FILE = ".display";
   
   private final AtomicReference<DisplayConfig> defaultConfig;
   private final Cache<String, DisplayConfig> sessionConfig;
   private final FileDirectorySource workspace;
   private final Persister persister;

   public DisplayPersister(FileDirectorySource workspace) {
      this.sessionConfig = new LeastRecentlyUsedCache<String, DisplayConfig>(2000);
      this.defaultConfig = new AtomicReference<DisplayConfig>();
      this.persister = new Persister();
      this.workspace = workspace;
   }
   
   public synchronized DisplayDefinition readDefinition(String session){
      return getDisplayFile(session).readDefinition();
   }
   
   public synchronized void saveDefinition(String session, DisplayDefinition definition) {
      getDisplayFile(session).saveDefinition(definition);
   }
   
   private synchronized DisplayConfig getDisplayFile(String session) {
      DisplayConfig cacheConfig = sessionConfig.fetch(session);
      
      if(cacheConfig == null) {
         DisplayConfig persistentConfig = defaultConfig.get();
         
         if(persistentConfig == null) {
            File file = workspace.createFile(DISPLAY_FILE);
            persistentConfig = new FileDisplayConfig(persister, file);
            defaultConfig.set(persistentConfig);
         }
         DisplayDefinition defaultDefinition = persistentConfig.readDefinition();

         if(session != null) {
            DisplayDefinition localCopy = copyDefinition(defaultDefinition);
            cacheConfig = new SessionDisplayConfig(session);
            cacheConfig.saveDefinition(localCopy);
            sessionConfig.cache(session, cacheConfig);
         } else {
            cacheConfig = persistentConfig;
         }
      }
      return cacheConfig;
   }
   
   private static DisplayDefinition copyDefinition(DisplayDefinition defaultDefinition) {
      Map<String, String> defaultFonts = defaultDefinition.getAvailableFonts();
      DisplayDefinition localCopy = new DisplayDefinition();
      Map<String, String> availableFonts = new LinkedHashMap<String, String>(defaultFonts);
      
      localCopy.setAvailableFonts(Collections.unmodifiableMap(availableFonts));
      localCopy.setThemeName(defaultDefinition.getThemeName());
      localCopy.setLogoImage(defaultDefinition.getLogoImage());
      localCopy.setConsoleCapacity(defaultDefinition.getConsoleCapacity());
      localCopy.setFontName(defaultDefinition.getFontName());
      localCopy.setFontSize(defaultDefinition.getFontSize());
      
      return localCopy;
   }
   
   private static interface DisplayConfig {
      void saveDefinition(DisplayDefinition definition);
      DisplayDefinition readDefinition();
   }
   
   private static class SessionDisplayConfig implements DisplayConfig {
      
      private final AtomicReference<DisplayDefinition> reference;
      private final String session;
      
      public SessionDisplayConfig(String session) {
         this.reference = new AtomicReference<DisplayDefinition>();
         this.session = session;
      }
      
      public void saveDefinition(DisplayDefinition definition) {
         reference.set(definition);
      }
      
      public DisplayDefinition readDefinition() {
         return reference.get();
      }
   }

   private static class FileDisplayConfig implements DisplayConfig {
      
      private final AtomicReference<DisplayDefinition> reference;
      private final FileExtension extension;
      private final Persister persister;
      private final File displayFile;
      private long loadTime;
      
      public FileDisplayConfig(Persister persister, File displayFile) {
         this.reference = new AtomicReference<DisplayDefinition>();
         this.extension = new FileExtension();
         this.displayFile = displayFile;
         this.persister = persister;
      }
      
      @Override
      public void saveDefinition(DisplayDefinition definition) {
         try {
            if(displayFile.exists()) {
               StringWriter writer = new StringWriter();
               persister.write(definition, writer);
               String text = writer.toString();
               byte[] data = text.getBytes("UTF-8");
               
               extension.writeBytes(displayFile, data);
               loadTime = displayFile.lastModified();
            }
         }catch(Exception e) {
            log.info("Could not save display", e);
         }
         reference.set(definition);
      }
      
      @Override
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