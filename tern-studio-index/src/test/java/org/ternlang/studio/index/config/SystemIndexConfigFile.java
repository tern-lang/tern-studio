package org.ternlang.studio.index.config;

import java.util.List;

import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.scan.ClassPathScanner;

public class SystemIndexConfigFile {

   public static IndexConfigFile getSystemClassPath() {
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      List<ClassFile> files = ClassPathScanner.scanBootstrapClassPath();
      
      return new SystemConfigFile(loader, files);
   }
   
   private static class SystemConfigFile extends IndexConfigFile {
      
      private final List<ClassFile> files;
      private final ClassLoader loader;
      
      public SystemConfigFile(ClassLoader loader, List<ClassFile> files) {
         this.loader = loader;
         this.files = files;
      }
      
      @Override
      public ClassLoader getClassLoader() {
         return loader;
      }
      
      @Override
      public List<ClassFile> getClassFiles() {
         return files;
      }
   }
}
