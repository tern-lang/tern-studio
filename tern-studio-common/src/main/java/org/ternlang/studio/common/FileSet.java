package org.ternlang.studio.common;

import java.io.File;
import java.util.List;

public class FileSet {

   private final List<File> files;
   private final File directory;
   private final long timeStamp;
   
   public FileSet(File directory, List<File> files, long timeStamp) {
      this.timeStamp = timeStamp;
      this.directory = directory;
      this.files = files;
   }
   
   public boolean isStale() {
      return DirectoryWatcher.lastModified(directory) > timeStamp;
   }
   
   public List<File> getFiles() {
      return files;
   }
   
   public long getTimeStamp(){
      return timeStamp;
   }
}
