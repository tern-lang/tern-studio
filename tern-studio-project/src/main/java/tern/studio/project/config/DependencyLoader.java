package tern.studio.project.config;

import java.util.List;

import tern.studio.project.maven.RepositoryFactory;

public interface DependencyLoader {
   List<DependencyFile> getDependencies(RepositoryFactory factory, List<Dependency> dependencies);
}