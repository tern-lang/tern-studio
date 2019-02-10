package tern.studio.common.find.text;

import java.io.File;

import lombok.AllArgsConstructor;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import tern.studio.common.FileDirectory;
import tern.studio.common.FileDirectorySource;
import tern.studio.common.RequestParser;

@AllArgsConstructor
public class TextMatchQueryParser {
   
   private static final String REPLACE = "replace";
   private static final String PATTERN = "pattern";
   private static final String EXPRESSION = "expression";
   private static final String CASE_SENSITIVE = "caseSensitive";
   private static final String REGULAR_EXPRESSION = "regularExpression";
   private static final String WHOLE_WORD = "wholeWord";
   private static final String ENABLE_REPLACE = "enableReplace";
   private static final String DEFAULT_PATTERN = "*.*";
   
   private final FileDirectorySource workspace;
   
   public TextMatchQuery parse(Request request) {
      RequestParser parser = new RequestParser(request);
      Path path = request.getPath();
      FileDirectory project = workspace.getByPath(path);
      
      if(project == null) {
         throw new IllegalStateException("Could not find project for " + path);
      }
      String name = project.getName();
      File root = project.getBasePath();
      String query = parser.getString(EXPRESSION);
      String replace = parser.getString(REPLACE, false);
      String pattern = parser.getString(PATTERN, DEFAULT_PATTERN);
      boolean caseSensitive = parser.getBoolean(CASE_SENSITIVE, false);
      boolean regularExpression = parser.getBoolean(REGULAR_EXPRESSION, false);
      boolean wholeWord = parser.getBoolean(WHOLE_WORD, false);
      boolean enableReplace = parser.getBoolean(ENABLE_REPLACE, false);
      
      return TextMatchQuery.builder()
            .pattern(pattern)
            .query(query)
            .path(root)
            .replace(replace)
            .project(name)
            .enableReplace(enableReplace)
            .caseSensitive(caseSensitive)
            .regularExpression(regularExpression)
            .wholeWord(wholeWord)
            .build();
   }
}