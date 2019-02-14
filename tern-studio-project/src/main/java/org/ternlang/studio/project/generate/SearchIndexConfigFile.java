package org.ternlang.studio.project.generate;

import java.util.List;

import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.config.IndexConfigFile;

public class SearchIndexConfigFile extends IndexConfigFile implements ConfigFile {
   
   private final List<ClassFile> files;
   private final ClassLoader loader;
   private final String text;
   
   public SearchIndexConfigFile(ClassLoader loader, List<ClassFile> files, String text) {
      this.loader = loader;
      this.files = files;
      this.text = text;
   }
   
   @Override
   public ClassLoader getClassLoader() {
      return loader;
   }
   
   @Override
   public List<ClassFile> getClassFiles() {
      return files;
   }

   @Override
   public String getConfigSource() {
      return text;
   }
}
