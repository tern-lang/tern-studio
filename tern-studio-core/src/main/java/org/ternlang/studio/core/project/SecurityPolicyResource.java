package org.ternlang.studio.core.project;

import org.simpleframework.http.Status;
import org.simpleframework.resource.MediaType;
import org.simpleframework.resource.action.ResponseEntity;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.Produces;
import org.ternlang.studio.project.Workspace;

import lombok.AllArgsConstructor;

@Path("/")
@AllArgsConstructor
public class SecurityPolicyResource {

   private final Workspace workspace;
   
   @GET
   @Path("/policy.*")
   @Produces("text/plain")
   public ResponseEntity getSecurityPolicy() {
      String policy = workspace.getSecurityPolicy();
      
      if(policy != null) {
         return ResponseEntity.create(Status.OK)
               .type(MediaType.TEXT_PLAIN)
               .entity(policy)
               .create();
      }
      return ResponseEntity.create(Status.NOT_FOUND).create();
   }
}
