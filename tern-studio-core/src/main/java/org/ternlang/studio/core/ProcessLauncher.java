package org.ternlang.studio.core;

import org.ternlang.studio.project.config.ProcessConfiguration;

public interface ProcessLauncher {   
   ProcessDefinition launch(ProcessConfiguration configuration) throws Exception;
}