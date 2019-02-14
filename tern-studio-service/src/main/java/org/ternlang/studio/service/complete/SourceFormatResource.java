package org.ternlang.studio.service.complete;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import lombok.AllArgsConstructor;

import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

// /format/<project>/<file>
@Path("/format")
@AllArgsConstructor(onConstructor=@__({@Inject}))
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
         String body) throws Exception
   {
      Project project = workspace.getByName(name);
      return formatter.format(project, path, body, indent);
   }
}