package org.ternlang.studio.project.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.project.config.DependencyResult;
import org.ternlang.studio.project.config.DependencyResultSet;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;

@Slf4j
public class RepositoryClient {
   
   private static final String EXTENSION_TYPE = "jar";

   private final List<RemoteRepository> repositories;
   private final Map<String, DependencyResultSet> cache;
   private final RepositoryFactory factory;
   private final RepositorySystem system;
   private final String path;

   public RepositoryClient(List<RemoteRepository> repositories, RepositorySystem system, RepositoryFactory factory, String path) {
      this.cache = new ConcurrentHashMap<String, DependencyResultSet>();
      this.repositories = repositories;
      this.factory = factory;
      this.system = system;
      this.path = path;
   }

   public DependencyResultSet resolve(String groupId, String artifactId, String version) throws Exception {
      String key = String.format("%s:%s:%s", groupId, artifactId, version);
      DependencyResultSet set = cache.get(key);
      
      if(set == null) {
         try {
            return download(key, groupId, artifactId, version);
         } catch(Exception e) {
            set = new DependencyResultSet(Collections.EMPTY_LIST, key, "Could not resolve '" + key + "'");
            
            if(log.isTraceEnabled()) {
               log.trace("Could not resolve '" + key + "'", e);
            }
         }
         cache.put(key, set);
      }
      return set;
   }
   
   private DependencyResultSet download(String key, String groupId, String artifactId, String version) throws Exception {
      List<DependencyResult> results = new ArrayList<DependencyResult>();
      Artifact artifact = new DefaultArtifact(groupId, artifactId, EXTENSION_TYPE, version);
      RepositorySystemSession session = factory.newRepositorySystemSession(system, path);
      DependencyFilter filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);

      CollectRequest request = new CollectRequest();
      Dependency dependency = new Dependency(artifact, JavaScopes.COMPILE, true); // make it optional
      DependencyRequest dependencyRequest = new DependencyRequest(request, filter);
      
      request.setRoot(dependency);

      for (RemoteRepository repository : repositories) {
         request.addRepository(repository);
      }
      org.sonatype.aether.resolution.DependencyResult dependencyResult = system.resolveDependencies(session, dependencyRequest);
      List<ArtifactResult> artifactResults = dependencyResult.getArtifactResults();

      for (ArtifactResult artifactResult : artifactResults) {
         String artifactArtifactId = artifactResult.getArtifact().getArtifactId();
         String artifactGroupId = artifactResult.getArtifact().getGroupId();
         String artifactVersion = artifactResult.getArtifact().getVersion();
         File localFile = artifactResult.getArtifact().getFile();
         File canonicalFile = localFile.getCanonicalFile();
         DependencyResult result = DependencyResult.builder()
               .artifactId(artifactArtifactId)
               .groupId(artifactGroupId)
               .version(artifactVersion)
               .file(canonicalFile)
               .build();
         
         results.add(result);
      }
      return new DependencyResultSet(results, key, null);
   }   
      
}