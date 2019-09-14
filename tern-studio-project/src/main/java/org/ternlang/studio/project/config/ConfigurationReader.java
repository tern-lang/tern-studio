package org.ternlang.studio.project.config;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;
import static org.ternlang.studio.project.config.ProjectConfiguration.PROJECT_FILE;
import static org.ternlang.studio.project.config.WorkspaceConfiguration.WORKSPACE_FILE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.util.Dictionary;
import org.simpleframework.xml.util.Entry;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.RemoteRepository;
import org.ternlang.studio.project.FileSystem;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.ProjectLayout;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.project.maven.RepositoryClient;
import org.ternlang.studio.project.maven.RepositoryFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigurationReader {
   
   private final AtomicReference<WorkspaceConfiguration> workspaceReference;
   private final Map<String, ProjectConfiguration> projectReference;
   private final ConfigurationFilter filter;
   private final RepositoryFactory factory;
   private final Persister persister;
   private final Workspace workspace;
   
   public ConfigurationReader(Workspace workspace) {
      this.projectReference = new ConcurrentHashMap<String, ProjectConfiguration>();
      this.workspaceReference = new AtomicReference<WorkspaceConfiguration>();
      this.factory = new RepositoryFactory();
      this.filter = new ConfigurationFilter();
      this.persister = new Persister(filter);
      this.workspace = workspace;
   }

   public WorkspaceConfiguration loadWorkspaceConfiguration() {
      WorkspaceConfiguration configuration = workspaceReference.get();
      
      if(configuration == null) {
         try {
            File file = workspace.createFile(WORKSPACE_FILE);
            
            if(file.exists()) {
               WorkspaceDefinition details = persister.read(WorkspaceDefinition.class, file);
               Map<String, String> locations = details.getRepositoryLocations();
               Map<String, String> variables = details.getEnvironmentVariables();
               List<String> arguments = details.getArguments();
               Set<String> repositories = locations.keySet();
               long limit = details.getTimeLimit();
               
               for(String repository : repositories) {
                  String location = locations.get(repository);
                  log.info("Repository: '" + repository + "' -> '" + location + "'");
               }
               configuration = new WorkspaceContext(factory, details, variables, arguments, limit);
               workspaceReference.set(configuration);
               return configuration;
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not read configuration", e);
         }
         return new WorkspaceContext(factory, null, EMPTY_MAP, EMPTY_LIST, TimeUnit.DAYS.toMillis(2));
      }
      return configuration;
   }  
   
   public ProjectConfiguration loadProjectConfiguration(String name) {
      try {
         ProjectConfiguration configuration = projectReference.get(name);
         
         if(isProjectConfigurationStale(name)) {
            Project project = workspace.createProject(name);
            FileSystem fileSystem = project.getFileSystem();
            File file = fileSystem.getFile(PROJECT_FILE);
            ProjectDefinition definition = persister.read(ProjectDefinition.class, file);
            
            projectReference.put(name, definition);
            return definition;
         } 
         if(configuration != null) {
            return configuration;
         }
         log.info("Project '" + name + "' does not contain a .project file");
      }catch(Exception e) {
         throw new IllegalStateException("Could not read .project file", e);
      }
      return new ProjectDefinition();
   }
   
   private boolean isProjectConfigurationStale(String name) {
      ProjectConfiguration configuration = projectReference.get(name);
      
      if(configuration == null) {
         return true;
      }
      Project project = workspace.createProject(name);
      FileSystem fileSystem = project.getFileSystem();
      File file = fileSystem.getFile(PROJECT_FILE);
      
      if(file.exists()) {
         long lastModified = file.lastModified();
         long configurationModification = configuration.getLastModifiedTime();
         
         if(lastModified > configurationModification) {
            return file.exists() && file.length() > 0;
         }
      }
      return false;
   }
   
   @Root
   private static class ProjectDefinition implements ProjectConfiguration {
      
      @Path("dependencies")
      @ElementList(entry="dependency", required=false, inline=true)
      private List<DependencyDefinition> dependencies;
      
      @ElementList(entry="variable", required=false)
      private Dictionary<VariableDefinition> properties;
      
      @Transient
      private Map<String, Object> attributes;
      
      @ElementList(entry="path", required=false)
      private List<String> source;
      
      private long lastModified;
      
      public ProjectDefinition() {
         this.lastModified = System.currentTimeMillis();
         this.dependencies = new ArrayList<DependencyDefinition>();
         this.properties = new Dictionary<VariableDefinition>();
         this.attributes = new ConcurrentHashMap<String, Object>();
         this.source = new ArrayList<String>();
      }

      @Override
      public Map<String, String> getProperties() {
         Map<String, String> map = new LinkedHashMap<String, String>();
         
         if(properties != null) {
            for(VariableDefinition data : properties) {
               map.put(data.name, data.value);
            }
         }
         return map;
      }
      
      @Override
      public List<Dependency> getDependencies() {
         return Collections.<Dependency>unmodifiableList(dependencies);
      }

      @Override
      public ProjectLayout getProjectLayout() {
         return new ProjectLayout(source.toArray(new String[]{}));
      }
      
      @Override
      public long getLastModifiedTime() {
         return lastModified;
      }

      @Override
      public <T> T getAttribute(String name) {
         return (T)attributes.get(name);
      }

      @Override
      public void setAttribute(String name, Object value) {
         if(value != null) {
            attributes.put(name, value);
         } else {
            attributes.remove(name);
         }
      }
   }
   
   @Root
   private static class WorkspaceDefinition implements DependencyLoader {
      
      @Element(name="repository", required=false)
      private RepositoryDefinition repository;
      
      @ElementList(entry="variable", required=false)
      private Dictionary<VariableDefinition> environment;
      
      @ElementList(entry="argument", required=false)
      private List<String> arguments;
      
      @Path("process")
      @Element(name="time-limit", required=false)
      private long limit;
      
      public WorkspaceDefinition() {
         this.environment = new Dictionary<VariableDefinition>();
         this.arguments = new ArrayList<String>();
         this.limit = TimeUnit.DAYS.toMillis(2);
      }
      
      @Override
      public List<DependencyFile> getDependencies(RepositoryFactory factory, List<Dependency> dependencies) {
         List<DependencyFile> files = new ArrayList<DependencyFile>();
         Set<String> done = new HashSet<String>();
         
         try {
            if(repository != null) {
               RepositoryClient client = repository.getClient(factory);
   
               if(dependencies != null) {
                  Map<String, DependencyResult> latestVersions = new LinkedHashMap<String, DependencyResult>();
                  List<DependencyResult> matchedResults = new ArrayList<DependencyResult>();
                  DependencyComparator comparator = new DependencyComparator(true);
                  
                  for (Dependency dependency : dependencies) {
                     Set<String> exclusions = dependency.getExclusions();
                     String groupId = dependency.getGroupId();
                     String artifactId = dependency.getArtifactId();
                     String version = dependency.getVersion();
                     String key = dependency.getDependencyFullName();
                     
                     if(done.add(key)) { // has this already been resolved
                        DependencyResultSet set = client.resolve(groupId, artifactId, version);
                        List<DependencyResult> matches = set.getResults();
                        String message = set.getMessage();
   
                        if(matches.isEmpty()) {
                           DependencyFile file = new DependencyFile(null, message);
                           files.add(file);
                        } else {
                           for (DependencyResult match : matches) {
                              String matchKey = match.getDependencyKey();
                              
                              if(!exclusions.contains(matchKey)) { // if its not excluded
                                 String matchName = match.getDependencyFullName();
                                 File matchFile = match.getFile();
                                 String matchPath = matchFile.getPath();
                                 
                                 if(!matchFile.exists()) {
                                    log.info("Could not resolve " + matchPath + " from " + key);
                                    match.setMessage("Could not resolve " + matchPath);
                                 }
                                 done.add(matchName);
                                 matchedResults.add(match);
                              } else {
                                 log.info("Excluding " + matchKey + " from " + key);
                              }
                           }
                        }
                     }
                  }
                  Collections.sort(matchedResults, comparator); // later versions first
                  
                  for(DependencyResult result : matchedResults) {
                     String resultKey = result.getDependencyKey();
                     String resultName = result.getDependencyFullName();
                     DependencyResult existing = latestVersions.get(resultKey);
                     
                     if(existing == null) {
                        latestVersions.put(resultKey, result);
                     } else {
                        String existingName = existing.getDependencyFullName();
                        
                        if(!existingName.equals(resultName)) {
                           log.info("Evicting " + resultName + " in favour of " + existingName);
                        }
                     }
                  }
                  Collection<DependencyResult> latestResults = latestVersions.values();
                  
                  for(DependencyResult result : latestResults) {
                     DependencyFile file = result.getDependencyFile();
                     files.add(file);
                  }
               }
            }
         } catch(Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
         }
         return Collections.unmodifiableList(files);
      }
      
      public Map<String, String> getRepositoryLocations() {
         Map<String, String> locations = new LinkedHashMap<String, String>();
         
         if(repository != null && repository.repositories != null) {
            for(LocationDefinition definition : repository.repositories) {
               locations.put(definition.name, definition.location);
            }
         }
         return locations;
      }

      public Map<String, String> getEnvironmentVariables() {
         Map<String, String> variables = new LinkedHashMap<String, String>();
         
         if(environment != null) {
            for(VariableDefinition data : environment) {
               variables.put(data.name, data.value);
            }
         }
         return variables;
      }
      
      public List<String> getArguments() {
         if(arguments != null) {
            return arguments;
         }
         return Collections.emptyList();
      }
      
      public long getTimeLimit() {
         return limit;
      }
   }
   
   
   @Root
   private static class RepositoryDefinition {
      
      @Attribute
      private String path;
      
      @ElementList(entry="location", inline=true)
      private List<LocationDefinition> repositories;
      
      public RepositoryClient getClient(RepositoryFactory factory) {
         List<RemoteRepository> list = new ArrayList<RemoteRepository>();
         
         for(LocationDefinition repository : repositories) {
            RemoteRepository remote = factory.newRemoteRepository(repository.name, "default", repository.location);
            list.add(remote);
         }
         RepositorySystem system = factory.newRepositorySystem();
         return new RepositoryClient(list, system, factory, path);
      }
   }
   
   @Root
   @Data
   private static class LocationDefinition implements Entry {
      
      @Text
      private String location;
      
      @Attribute
      private String name;
   }
   
   @Root
   @Data
   private static class DependencyDefinition extends Dependency {

      @Element
      private String groupId;
      
      @Element
      private String artifactId;
      
      @Element
      private String version;
      
      @ElementList(required=false)
      private List<ExclusionDefinition> exclusions;
      
      @Override
      public Set<String> getExclusions() {
         Set<String> excludedKeys = new HashSet<String>();
         
         if(exclusions != null) {
            for(ExclusionDefinition exclusion : exclusions) {
               String excludedKey = exclusion.getDependencyKey();
               excludedKeys.add(excludedKey);
            }
         }
         return Collections.unmodifiableSet(excludedKeys);
      }
   }
   
   @Root
   @Data
   private static class ExclusionDefinition extends Dependency {

      @Element
      private String groupId;
      
      @Element
      private String artifactId;
      
      @Override
      public String getVersion() {
         return null;
      }
   }
   
   @Root
   private static class VariableDefinition implements Entry {
      
      @Attribute
      private String name;
      
      @Text
      private String value;
      
      @Commit
      public void update(Map session) {
         session.put(name, value);
      }
      
      @Override
      public String getName() {
         return name;
      }
   }
   
   @Root
   private static class CommandTemplate implements Entry {
      
      @Attribute
      private OperatingSystem type;
      
      @Text
      private String value;
      
      @Override
      public String getName() {
         return type.name();
      }
   }
}