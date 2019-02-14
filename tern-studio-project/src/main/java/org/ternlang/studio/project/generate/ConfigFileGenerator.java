package org.ternlang.studio.project.generate;

import java.io.File;

import org.ternlang.studio.project.Project;

public interface ConfigFileGenerator {
   ConfigFile generateConfig(Project project);
   ConfigFile parseConfig(Project project, String source);
   File getConfigFilePath(Project project);
   String getConfigName(Project project);
}
