package org.ternlang.studio.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class FilePatternMatcher {

   public static List<File> scan(Pattern pattern, File directory) throws Exception {
      PatternFilter filter = new PatternFilter(pattern);
      return scan(filter, directory);
   }
   
   public static List<File> scan(FilenameFilter filter, File directory) throws Exception {
      List<File> files = gather(directory);
      List<File> normalized = normalize(files);
      Map<String, File> filtered = filter(filter, normalized);
      Collection<File> results = filtered.values();
      
      if(!results.isEmpty()) {
         List<File> list = new ArrayList<File>();
         
         for(File file : results) {
            list.add(file);
         }
         return list;
      }
      return Collections.emptyList();
   }
   
   private static Map<String, File> filter(FilenameFilter filter, List<File> files) throws Exception {
      Map<String, File> result = new TreeMap<String, File>();
      
      for(File file : files) {
         String path = file.getPath();
         
         if(filter.accept(file, path)) {
            result.put(path, file);
         }
      }
      return result;
   }
   
   private static List<File> normalize(List<File> files) throws Exception {
      List<File> result = new ArrayList<File>();
      
      for(File file : files) {
         File canonical = file.getCanonicalFile();
         
         if(canonical.exists()) {
            result.add(canonical);
         }
      }
      return result;
   }
   
   private static List<File> gather(File directory) throws Exception {
      List<File> files = new ArrayList<File>();
      
      if(directory.exists()) {
         File[] list = directory.listFiles();

         if(list != null) {
            for(File entry : list) {
               if(entry.isDirectory()) {
                  List<File> matches = gather(entry);
                  
                  if(!matches.isEmpty()) {
                     files.addAll(matches);
                  }
               } else {
                  files.add(entry);
               }
            }
         }
      }
      return files;
   }
   
   
   private static class PatternFilter implements FilenameFilter {
      
      private final Pattern pattern;
      
      public PatternFilter(Pattern pattern) {
         this.pattern = pattern;
      }

      @Override
      public boolean accept(File dir, String name) {
         return pattern.matcher(name).matches();
      }
      
   }
}