package tern.studio.common;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilePatternWalker {

   private final Map<String, FileSet> cache;
   
   public FilePatternWalker() {
      this.cache = new ConcurrentHashMap<String, FileSet>();
   }
   
   public List<File> walk(String pattern) throws IOException {
      FileSet set = cache.get(pattern);
      
      if(set == null || set.isStale()) {
         set = FilePatternScanner.scan(pattern);
         cache.put(pattern, set);
      }
      return set.getFiles();
   }
}
