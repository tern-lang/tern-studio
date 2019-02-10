package tern.studio.project;

import java.io.File;
import java.util.Collections;
import java.util.List;

import tern.studio.common.DirectoryWatcher;

public class FileData {

   private final FileSystem fileSystem;
   private final String location;
   private final File file;
   private final long time;
   private List<FileData> files;
   private String text;
   private byte[] data;
   
   public FileData(FileSystem fileSystem, String location, File file, long time) {
      this.fileSystem = fileSystem;
      this.location = location;
      this.file = file;
      this.time = time;
   }
   
   public File getFile() {
      return file;
   }
   
   public List<FileData> getFiles() {
      try {
         if(file == null) {
            files = Collections.emptyList();
         }
         if(files == null) {
            files = fileSystem.readFiles(file);
         }
      } catch(Exception e){
         throw new IllegalStateException("Could not list files for " + location, e);
      }
      return files;
   }
   
   public byte[] getByteArray() {
      try {
         if(data == null) {
            data = fileSystem.readAsByteArray(location);
         }
      } catch(Exception e){
         throw new IllegalStateException("Could not encode " + location, e);
      }
      return data;
   }
   
   public String getString() {
      try {
         if(text == null) {
            text = fileSystem.readAsString(location);
         }
      } catch(Exception e){
         throw new IllegalStateException("Could not encode " + location, e);
      }
      return text;
   }
   
   public boolean isStale() {
      if(file != null) {
         if(file.isDirectory()) {
            return DirectoryWatcher.lastModified(file) > time;
         }
         return file.lastModified() > time;
      }
      return true;
   }
   
   public long getLastModified(){
      if(file != null) {
         return file.lastModified();
      }
      return -1;
   }
}