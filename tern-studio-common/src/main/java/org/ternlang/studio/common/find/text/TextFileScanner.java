package org.ternlang.studio.common.find.text;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ternlang.studio.common.find.file.FileMatch;
import org.ternlang.studio.common.find.file.FileMatchScanner;

public class TextFileScanner {
   
   private final FileMatchScanner scanner;
   
   public TextFileScanner() {
      this.scanner = new FileMatchScanner();
   }

   public Set<TextFile> findAllFiles(TextMatchQuery query) throws Exception {
      Set<FileMatch> filesFound = new LinkedHashSet<FileMatch>();
      Set<TextFile> textFiles = new LinkedHashSet<TextFile>();
      String filePattern = query.getPattern();
      File directory = query.getPath();
      String project = query.getProject();
      
      if(filePattern == null) {
         filePattern = "*.*";
      }
      String[] fileExpressions = filePattern.split(",");
      
      for(String fileExpression : fileExpressions) {
         String pathPattern = fileExpression.trim();
         
         if(!pathPattern.isEmpty()) {
            List<FileMatch> filesMatched = scanner.findAllFiles(directory, project, pathPattern);
            
            for(FileMatch fileMatch : filesMatched) {
               filesFound.add(fileMatch);
            }
         }
      }
      for(FileMatch fileMatch : filesFound) {
         File file = fileMatch.getFile();
         String resourcePath = fileMatch.getResource();
         String projectName = fileMatch.getProject();
         TextFile projectFile = new TextFile(file, projectName, resourcePath);
         textFiles.add(projectFile);
      }
      return textFiles;
   }
}