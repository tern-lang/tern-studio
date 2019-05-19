package org.ternlang.studio.common.find.text;

import java.io.File;
import java.util.List;

import org.ternlang.service.annotation.DefaultValue;
import org.ternlang.service.annotation.GET;
import org.ternlang.service.annotation.Path;
import org.ternlang.service.annotation.PathParam;
import org.ternlang.service.annotation.Produces;
import org.ternlang.service.annotation.QueryParam;
import org.ternlang.studio.common.FileDirectory;
import org.ternlang.studio.common.FileDirectorySource;

import lombok.AllArgsConstructor;

@Path("/find")
@AllArgsConstructor
public class TextMatchResource {
   
   private static final int MAX_COUNT = 1000;
   
   private final FileDirectorySource workspace;
   private final TextMatchScanner scanner;

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