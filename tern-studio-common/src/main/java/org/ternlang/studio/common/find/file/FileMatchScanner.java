package org.ternlang.studio.common.find.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simpleframework.http.parse.PathParser;
import org.ternlang.studio.common.FilePatternMatcher;
import org.ternlang.studio.common.find.ExpressionResolver;
import org.ternlang.studio.common.find.MatchEvaluator;
import org.ternlang.studio.common.find.MatchType;
import org.ternlang.studio.common.find.PathBuilder;

public class FileMatchScanner {

   public List<FileMatch> findAllFiles(File directory, String project, String expression) throws Exception {
      String root = directory.getCanonicalPath();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      List<FileMatch> filesFound = new ArrayList<FileMatch>();
      PathBuilder builder = new PathBuilder(root);
      ExpressionResolver resolver = new ExpressionResolver(expression);
      FileExpressionFilter filter = new FileExpressionFilter(resolver, builder);
      
      List<File> list = FilePatternMatcher.scan(filter, directory);
      
      for(File file : list) {
         String resourcePath = builder.buildPath(file);
         String textMatch = resolver.match(resourcePath);
         
         if(textMatch != null) {
            MatchEvaluator evaluator = MatchEvaluator.of(MatchType.LITERAL, textMatch, false);
            String replaceText = evaluator.match(resourcePath);
            PathParser parser = new PathParser(resourcePath);
            FileMatch projectFile = new FileMatch(
                  project, 
                  resourcePath, 
                  parser.getDirectory(),
                  parser.getName(),
                  file, 
                  replaceText);
            
            filesFound.add(projectFile);
         }
      }
      Collections.sort(filesFound);
      return filesFound;
   }
}