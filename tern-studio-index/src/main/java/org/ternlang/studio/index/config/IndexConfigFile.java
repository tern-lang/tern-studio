package org.ternlang.studio.index.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.classpath.ClassIndexProcessor;
import org.ternlang.studio.index.scan.ClassPathScanner;

@Slf4j
public abstract class IndexConfigFile {
   
   private final IndexConfigHelper helper;
   
   public IndexConfigFile() {
      this.helper = new IndexConfigHelper(this);
   }
   
   public Set<IndexNode> getAllProjectClasses() {
      return helper.getProjectClassPath();
   }
   
   public Set<IndexNode> getBootstrapClasses() {
      return helper.getBootstrapClasses();
   }
   
   public Map<String, IndexNode> getDefaultImportClasses() {
      return helper.getDefaultImportClasses();
   }
   
   public Map<String, IndexNode> getDefaultImportNames() {
      return helper.getDefaultImportNames();
   }

   public ClassFile getClassFile(Class type) {
      String path = helper.getResourceName(type);
      try {
         return ClassPathScanner.createClassFile(path);
      } catch(Exception e) {
         log.info("Could not get file", e);
      }
      return null;
   }
   
   public IndexNode getIndexNode(Class type) {
      String name = type.getCanonicalName();
      IndexNode node = getDefaultImportClasses().get(name);
      
      if(node == null) {
         try {
            ClassFile file = getClassFile(type);
            return ClassIndexProcessor.getIndexNode(file);
         } catch(Exception e) {
            log.info("Could not get node", e);
         }
      }
      return node;
   }
   
   public abstract ClassLoader getClassLoader();
   public abstract List<ClassFile> getClassFiles();
}
