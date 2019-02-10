package tern.studio.service;

import tern.studio.project.config.ProcessConfiguration;

public interface ProcessLauncher {   
   ProcessDefinition launch(ProcessConfiguration configuration) throws Exception;
}