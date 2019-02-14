package org.ternlang.studio.project.config;

import java.util.Collections;
import java.util.Set;

public abstract class Dependency {
   public abstract String getGroupId();
   public abstract String getArtifactId();
   public abstract String getVersion(); 
   
   public Set<String> getExclusions() {
      return Collections.emptySet();
   }

   public String getDependencyKey(){
      String groupId = getGroupId();
      String artifactId = getArtifactId();
      
      return String.format("%s:%s", groupId, artifactId);
   }
   
   public String getDependencyFullName(){
      String groupId = getGroupId();
      String artifactId = getArtifactId();
      String version = getVersion();
      
      if(version != null) {
         return String.format("%s:%s:%s", groupId, artifactId, version);
      }
      return String.format("%s:%s", groupId, artifactId);
   }
}
