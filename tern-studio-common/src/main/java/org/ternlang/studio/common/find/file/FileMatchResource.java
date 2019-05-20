package org.ternlang.studio.common.find.file;

import java.io.File;
import java.util.List;

import org.ternlang.service.resource.annotation.GET;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.PathParam;
import org.ternlang.service.resource.annotation.Produces;
import org.ternlang.service.resource.annotation.QueryParam;
import org.ternlang.studio.common.FileDirectory;
import org.ternlang.studio.common.FileDirectorySource;

@Path("/file")
public class FileMatchResource {

   private final FileDirectorySource workspace;
   private final FileMatchScanner scanner;
   
   public FileMatchResource(FileDirectorySource workspace) {
      this.scanner = new FileMatchScanner();
      this.workspace = workspace;
   }
   
   @GET
   @Produces("application/json")
   @Path("/{project}")
   public List<FileMatch> findFiles(
         @PathParam("project") String name, 
         @QueryParam("expression") String expression) throws Exception 
   {
      FileDirectory project = workspace.getByName(name);
      File directory = project.getBasePath();
      
      return scanner.findAllFiles(directory, name, expression);
   }
}