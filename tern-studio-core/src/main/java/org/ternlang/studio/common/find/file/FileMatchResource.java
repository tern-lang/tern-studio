package org.ternlang.studio.common.find.file;

import java.io.File;
import java.util.List;

import org.ternlang.studio.common.FileDirectory;
import org.ternlang.studio.common.FileDirectorySource;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;
import org.ternlang.studio.resource.action.annotation.QueryParam;

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