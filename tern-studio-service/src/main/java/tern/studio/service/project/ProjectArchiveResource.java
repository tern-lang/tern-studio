package tern.studio.service.project;

import static org.simpleframework.http.Protocol.CONTENT_DISPOSITION;
import static org.simpleframework.http.Protocol.DATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.project.Project;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ResourcePath("/archive/.*")
public class ProjectArchiveResource implements Resource {

   private final Workspace workspace;

   public ProjectArchiveResource(Workspace workspace){
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath();
      Project project = workspace.getByPath(path);
      String[] pathSegments = path.getSegments(); // /archive/<project>/<archive>/<main-script-path>
      String archiveName = pathSegments[2]; // <archive>
      String projectName = project.getName();
      String mainScript = path.getPath(3); // /<main-script-path>
      
      log.info("Creating archive {}.jar from {} using {}", archiveName, projectName, mainScript);

      long time = System.currentTimeMillis();
      
      response.setStatus(Status.OK);
      response.setDate(DATE, time);
      
      try {
         File archiveFile = project.getExportedArchive(mainScript);
         OutputStream output = response.getOutputStream();
         
         response.setContentType("application/octet-stream");
         response.setValue(CONTENT_DISPOSITION, "attachment; filename=" + archiveName + ".jar;");
         
         InputStream source = new FileInputStream(archiveFile);
         
         try {
            IOUtils.copy(source, output);
         } finally {
            source.close();
            output.close();
         }
      } catch(Exception cause) {
         PrintStream output = response.getPrintStream();
         
         response.setStatus(Status.INTERNAL_SERVER_ERROR);
         response.setContentType("text/plain");
         cause.printStackTrace(output);
         output.close();
      }
   }
}