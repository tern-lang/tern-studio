package tern.studio.common.resource.display;

import static org.simpleframework.http.Protocol.CONTENT_ENCODING;
import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Status.OK;

import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import tern.studio.common.FileDirectorySource;
import tern.studio.common.resource.Resource;

@Slf4j
public class DisplayFileResource implements Resource {

   private final DisplayContentProcessor displayProcessor;
   private final FileDirectorySource workspace;
   private final Status status;

   public DisplayFileResource(DisplayContentProcessor displayProcessor, FileDirectorySource workspace) {
      this(displayProcessor, workspace, OK);
   }

   public DisplayFileResource(DisplayContentProcessor displayProcessor, FileDirectorySource workspace, Status status) {
      this.displayProcessor = displayProcessor;
      this.workspace = workspace;
      this.status = status;
   }

   @Override
   public void handle(Request request, Response response) throws Exception {
      DisplayContent content = displayProcessor.create(request);
      OutputStream output = response.getOutputStream();
      String type = content.getType();
      String path = content.getPath();
      String encoding = content.getEncoding();
      byte[] data = content.getData();
      double ratio = content.getCompression();
      long time = content.getDuration();

      if(log.isTraceEnabled()) {
         log.trace(path + " ratio=" + ratio + "% time=" + time + "ms");
      }
      response.setCode(status.code);
      response.setDescription(status.description);
      response.setValue(CONTENT_TYPE, type);
      
      if(encoding != null){
         response.setValue(CONTENT_ENCODING, encoding);
      }
      response.setContentLength(data.length);
      output.write(data);
      output.close();
   }
}