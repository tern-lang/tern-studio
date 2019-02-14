package org.ternlang.studio.common.find.file;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.ternlang.studio.common.FileDirectory;
import org.ternlang.studio.common.FileDirectorySource;

@Path("/file")
public class FileMatchResource {

   private final FileDirectorySource workspace;
   private final FileMatchScanner scanner;
   
   @Inject
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