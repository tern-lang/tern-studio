package tern.studio.common.find.file;

import java.io.File;

import lombok.AllArgsConstructor;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import tern.studio.common.FileDirectory;
import tern.studio.common.FileDirectorySource;
import tern.studio.common.RequestParser;

@AllArgsConstructor
public class FileMatchQueryParser {

   private static final String EXPRESSION = "expression";
   
   private final FileDirectorySource workspace;
   
   public FileMatchQuery parse(Request request) {
      RequestParser parser = new RequestParser(request);
      Path path = request.getPath();
      FileDirectory project = workspace.getByPath(path);
      
      if(project == null) {
         throw new IllegalStateException("Could not find project for " + path);
      }
      String name = project.getName();
      File root = project.getBasePath();
      String query = parser.getString(EXPRESSION);
      
      return FileMatchQuery.builder()
            .query(query)
            .path(root)
            .project(name)
            .build();
   }
}