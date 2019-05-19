package org.ternlang.studio.service.project.file;

import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.resource.action.annotation.Component;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Component
@AllArgsConstructor
public class DownloadService {

   private final FileService service;
   private final Workspace workspace;

   @SneakyThrows
   public FileResult findFile(String name, String path) {
      Project project = workspace.getByName(name);
      String real = project.getRealPath(path);

      return service.findFile(name, real);
   }
}
