package tern.studio.project.generate;

import java.io.File;
import java.util.Collections;
import java.util.List;

import tern.studio.project.ClassPathFile;

public class ClassPathConfigFile implements ConfigFile, ClassPathFile {

   private final List<String> errors;
   private final List<File> files;
   private final String path;
   
   public ClassPathConfigFile(List<File> files, String path) {
      this(files, path, Collections.EMPTY_LIST);
   }
   
   public ClassPathConfigFile(List<File> files, String path, List<String> errors) {
      this.errors = Collections.unmodifiableList(errors);
      this.files = Collections.unmodifiableList(files);
      this.path = path;
   }
   
   @Override
   public String getPath(){
      return path;
   }
   
   public List<File> getFiles(){
      return files;
   }
   
   @Override
   public List<String> getErrors() {
      return errors;
   }

   @Override
   public String getConfigSource() {
      return path;
   }
}
