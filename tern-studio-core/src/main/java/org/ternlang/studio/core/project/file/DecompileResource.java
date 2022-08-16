package org.ternlang.studio.core.project.file;

import org.simpleframework.resource.annotation.CacheControl;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;

import lombok.AllArgsConstructor;

@Path("/resource")
@AllArgsConstructor
public class DecompileResource {

   private final DecompileService service;

   @GET
   @Produces("text/plain")
   @Path("/{project}/decompile/{jar}.jar/{module}/{name}.java")
   @CacheControl("no-cache, max-age=0, must-revalidate, proxy-revalidate")
   public byte[] decompile(
        @PathParam("project") String project,
        @PathParam("jar") String jar,
        @PathParam("module") String module,
        @PathParam("name") String name) {
      try {
         FileResult result = service.decompile(jar + ".jar", module + "." + name);
         return result.getData();
      } catch (Exception e) {
         return String.format("// could not decompile %s.%s", module, name).getBytes();
      }
   }

   @GET
   @Produces("text/plain")
   @Path("/{project}/decompile/{path}/java\\.[a-z\\.]+/{module}/{name}.java")
   @CacheControl("no-cache, max-age=0, must-revalidate, proxy-revalidate")
   public byte[] decompileJava(
        @PathParam("project") String project,
        @PathParam("path") String path,
        @PathParam("module") String module,
        @PathParam("name") String name) {
      try {
         FileResult result = service.decompile(module + "." + name);
         return result.getData();
      } catch (Exception e) {
         return String.format("// could not decompile %s.%s", module, name).getBytes();
      }
   }

   @GET
   @Produces("text/plain")
   @Path("/{project}/decompile/{path}/jdk\\.[a-z\\.]+/{module}/{name}.java")
   @CacheControl("no-cache, max-age=0, must-revalidate, proxy-revalidate")
   public byte[] decompileJdk(
        @PathParam("project") String project,
        @PathParam("path") String path,
        @PathParam("module") String module,
        @PathParam("name") String name) {
      try {
         FileResult result = service.decompile(module + "." + name);
         return result.getData();
      } catch (Exception e) {
         return String.format("// could not decompile %s.%s", module, name).getBytes();
      }
   }
}
