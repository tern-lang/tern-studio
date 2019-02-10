package tern.studio.common;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class FilePatternScanner {
   
   private static final String RECURSIVE_PATTERN = "_RECURSIVE_PATTERN_";
   private static final String SINGLE_PATTERN = "_SINGLE_PATTERN_";
   
   public static FileSet scan(String token, File root) throws IOException {
      File file = new File(root, token);
      
      if(root.exists()) {
         String expand = file.getAbsolutePath();
         return scan(expand);
      }
      return new FileSet(root, Collections.EMPTY_LIST, 0);
   }
   
   public static FileSet scan(String token) throws IOException {
      File file = new File(token);
      
      if(token.contains("*")) {
         int index = token.indexOf("*");
         long time = System.currentTimeMillis();
         String expression = token.trim();
         
         if(index != -1) {
            String parent = token.substring(0, index);
            File directory = new File(parent);
            
            if(directory.exists()) {
               expression = expression.replace("**", RECURSIVE_PATTERN); // convert \** to \.*
               expression = expression.replace("*", SINGLE_PATTERN); // convert \* to file regex
               
               File path = new File(expression);
               
               try {
                  expression = path.getCanonicalPath(); // remove ../ and ./
                  expression = PatternEscaper.escape(expression);
                  expression = expression.replace(RECURSIVE_PATTERN, ".*");
                  expression = expression.replace(SINGLE_PATTERN, "[a-zA-Z0-9_\\$\\-\\(\\)\\.\\s]+");
                  
                  Pattern pattern = Pattern.compile(expression);
                  List<File> list = FilePatternMatcher.scan(pattern, directory);
                  
                  Collections.sort(list);
                  
                  return new FileSet(directory, list, time);
               }catch(Exception e) {
                  throw new IllegalArgumentException("Could not parse pattern '" +token+ "'", e);
               }
            }
         }
      }
      if(file.exists()) {
         long time = System.currentTimeMillis();
         File directory = file.getParentFile();
         List<File> files = Collections.singletonList(file);
         
         return new FileSet(directory, files, time);
      }
      return new FileSet(file, Collections.EMPTY_LIST, 0);
   }
}