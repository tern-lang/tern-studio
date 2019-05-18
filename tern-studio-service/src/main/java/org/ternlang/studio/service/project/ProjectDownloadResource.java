package org.ternlang.studio.service.project;

import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.resource.ContentTypeResolver;
import org.ternlang.studio.resource.ResourcePath;
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
