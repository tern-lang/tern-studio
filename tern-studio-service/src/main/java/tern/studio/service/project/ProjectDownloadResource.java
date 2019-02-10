package tern.studio.service.project;

import tern.studio.common.resource.ContentTypeResolver;
import tern.studio.common.resource.ResourcePath;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/download/.*")
public class ProjectDownloadResource extends ProjectFileResource {

   public ProjectDownloadResource(Workspace workspace, ContentTypeResolver resolver) {
      super(workspace, resolver);
   }
   
   @Override
   public boolean isDownload(){
      return true;
   }

}
