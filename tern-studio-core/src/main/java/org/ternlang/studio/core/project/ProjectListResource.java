package org.ternlang.studio.core.project;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.Produces;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

import lombok.AllArgsConstructor;

@Path("/")
@AllArgsConstructor
public class ProjectListResource {

   private final Workspace workspace;
   
   @GET
   @Path("/projects/list")
   @Produces("application/json")
   public Map<String, String> listProjects() {
      return workspace.getProjects()
            .stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                  Project::getName, 
                  project -> project.getBasePath().getAbsolutePath()));
   }
}
