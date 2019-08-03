package org.ternlang.studio.core.complete;

import org.simpleframework.module.annotation.DefaultValue;
import org.simpleframework.resource.annotation.Body;
import org.simpleframework.resource.annotation.POST;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;
import org.simpleframework.resource.annotation.QueryParam;
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
   @Path("{project}/{path}")
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