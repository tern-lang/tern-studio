package org.ternlang.studio.index.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.core.Reserved;
import org.ternlang.core.link.ImportPathResolver;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.classpath.ClassIndexProcessor;

@Slf4j
class IndexConfigHelper {
   
   private final Map<String, IndexNode> defaultImportNames;
   private final Map<String, IndexNode> defaultImportClasses;
   private final ImportPathResolver importResolver;
   private final Set<IndexNode> bootstrapClasses;
   private final Set<IndexNode> allClasses;
   private final IndexConfigFile config;
   
   public IndexConfigHelper(IndexConfigFile config) {
       this.importResolver = new ImportPathResolver(Reserved.IMPORT_FILE); 
       this.defaultImportNames = new ConcurrentHashMap<String, IndexNode>();
       this.defaultImportClasses = new ConcurrentHashMap<String, IndexNode>();
       this.bootstrapClasses = new CopyOnWriteArraySet<IndexNode>();
       this.allClasses = new CopyOnWriteArraySet<IndexNode>();
       this.config = config;
   }
   
   public synchronized Set<IndexNode> getProjectClassPath() {
      if(allClasses.isEmpty()) {
         try {
            List<ClassFile> projectClasses = config.getClassFiles();
         
            for(ClassFile file : projectClasses) {
               IndexNode node = ClassIndexProcessor.getIndexNode(file);
               allClasses.add(node);
            }
         } catch(Throwable e) {
            log.info("Could not load index node", e);
         }
      }
      return Collections.unmodifiableSet(allClasses);
   }

   public synchronized Set<IndexNode> getBootstrapClasses() {
      if(bootstrapClasses.isEmpty()) {
         try {
            List<ClassFile> files = config.getClassFiles();
            
            for(ClassFile file : files) {
               IndexNode node = ClassIndexProcessor.getIndexNode(file);
               bootstrapClasses.add(node);
            }
         } catch (Throwable e) {
            log.info("Could not load index node", e);
         }
      }
      return Collections.unmodifiableSet(bootstrapClasses);
   }
   
   public synchronized Map<String, IndexNode> getDefaultImportClasses() {
      if(defaultImportClasses.isEmpty()) {
         Set<IndexNode> nodes = getBootstrapClasses();
         Map<String, IndexNode> names = getDefaultImportNames();

         for(IndexNode node : nodes) {
            String fullName = node.getFullName();
            String alias = importResolver.resolveName(fullName);
            
            if(!fullName.equals(alias)) { 
               defaultImportClasses.put(alias, node);
            }
            defaultImportClasses.put(fullName, node);
         }
         defaultImportClasses.putAll(names);
      }
      return Collections.unmodifiableMap(defaultImportClasses);
   }
   
   public synchronized Map<String, IndexNode> getDefaultImportNames() {
      if(defaultImportNames.isEmpty()) {
         Set<IndexNode> nodes = getBootstrapClasses();

         for(IndexNode node : nodes) {
            String shortName = node.getName();
            String fullName = node.getFullName();
            List<String> resolvePath = importResolver.resolvePath(shortName);
            
            if(resolvePath.contains(fullName)) {
               defaultImportNames.put(shortName, node);
            }
         }
      }
      return Collections.unmodifiableMap(defaultImportNames);
   }
   
   public synchronized String getResourceName(Class type) {
      return type.getCanonicalName().replace('.', '/') + ".class";
   }
}
