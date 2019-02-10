package tern.studio.common.find.text;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import tern.studio.common.FileDirectory;
import tern.studio.common.FileDirectorySource;

@Path("/find")
public class TextMatchResource {
   
   private static final int MAX_COUNT = 1000;
   
   private final FileDirectorySource workspace;
   private final TextMatchScanner scanner;
   
   @Inject
   public TextMatchResource(FileDirectorySource workspace, TextMatchScanner scanner) {
      this.workspace = workspace;
      this.scanner = scanner;
   }
   
   @GET
   @Path("/{project}")
   @Produces("application/json")
   public List<TextMatch> findMatches(
         @PathParam("project") String project,
         @QueryParam("expression") String expression,
         @QueryParam("replace") String replace,
         @QueryParam("pattern") @DefaultValue(".*") String pattern,
         @QueryParam("caseSensitive") @DefaultValue("false") boolean caseSensitive,
         @QueryParam("regularExpression") @DefaultValue("false") boolean regularExpression,
         @QueryParam("wholeWord") @DefaultValue("false") boolean wholeWord,
         @QueryParam("enableReplace") @DefaultValue("false") boolean enableReplace) throws Exception 
   {
      FileDirectory directory = workspace.getByName(project);
      File root = directory.getBasePath();
      TextMatchQuery query = TextMatchQuery.builder()
            .pattern(pattern)
            .query(expression)
            .path(root)
            .replace(replace)
            .project(project)
            .enableReplace(enableReplace)
            .caseSensitive(caseSensitive)
            .regularExpression(regularExpression)
            .wholeWord(wholeWord)
            .build();
   
      List<TextMatch> matches = scanner.process(query);
      int length = matches.size();
      
      if(length > MAX_COUNT) {
         return matches.subList(0, MAX_COUNT);
      }
      return matches;
   }
}