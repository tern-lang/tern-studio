package tern.studio.project.config;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DependencyResult extends Dependency {

   private String groupId;
   private String artifactId;
   private String version;
   private String message;
   private File file;
   
   public DependencyFile getDependencyFile(){
      return new DependencyFile(file, message);
   }
}
