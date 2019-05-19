package org.ternlang.studio.service.complete;

import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.resource.action.annotation.Body;
import org.ternlang.studio.resource.action.annotation.DefaultValue;
import org.ternlang.studio.resource.action.annotation.POST;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;
import org.ternlang.studio.resource.action.annotation.QueryParam;

import lombok.AllArgsConstructor;

// /format/<project>/<file>
@Path("/format")
@AllArgsConstructor
public class SourceFormatResource {

   private final SourceFormatter formatter;
   private final Workspace workspace;

   @POST
   @Path("{project}/{path:.+}")
   @Produces("text/plain")
   public String formatSource(
         @PathParam("project") String name, 
         @PathParam("path") String path, 
         @QueryParam("indent") @DefaultValue("3") int indent,
         @Body String body) throws Exception
   {
      Project project = workspace.getByName(name);
      return formatter.format(project, path, body, indent);
   }
}