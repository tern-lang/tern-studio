package org.ternlang.studio.project.config;

import java.util.List;

import org.ternlang.studio.project.maven.RepositoryFactory;

public interface DependencyLoader {
   List<DependencyFile> getDependencies(RepositoryFactory factory, List<Dependency> dependencies);
}