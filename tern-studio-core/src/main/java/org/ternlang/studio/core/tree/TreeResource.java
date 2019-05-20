package org.ternlang.studio.core.tree;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.service.resource.annotation.GET;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.QueryParam;

import lombok.AllArgsConstructor;

@Path("/tree")
@AllArgsConstructor
public class TreeResource {
   
   private final TreeService service;

   @GET
   @Path("$")
   public void workspace(
         @QueryParam("id") String name,
         @QueryParam("expand") String expand,
         @QueryParam("folders") String folders,
         @QueryParam("depth") String depth,
         Request request, 
         Response response) 
   {
      service.tree(name, expand, folders, depth, request, response);
   }
   
   @GET
   @Path("/.*")
   public void project(
         @QueryParam("id") String name,
         @QueryParam("expand") String expand,
         @QueryParam("folders") String folders,
         @QueryParam("depth") String depth,
         Request request, 
         Response response) 
   {
      service.tree(name, expand, folders, depth, request, response);
   }
}