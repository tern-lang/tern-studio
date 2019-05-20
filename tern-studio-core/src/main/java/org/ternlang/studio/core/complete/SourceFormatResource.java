package org.ternlang.studio.core.complete;

import org.ternlang.service.annotation.DefaultValue;
import org.ternlang.service.resource.annotation.Body;
import org.ternlang.service.resource.annotation.POST;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.PathParam;
import org.ternlang.service.resource.annotation.Produces;
import org.ternlang.service.resource.annotation.QueryParam;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

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