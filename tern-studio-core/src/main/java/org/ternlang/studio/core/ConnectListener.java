package org.ternlang.studio.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

import lombok.AllArgsConstructor;

import org.simpleframework.http.Path;
import org.simpleframework.module.annotation.Component;
import org.ternlang.studio.core.command.CommandListener;
import org.ternlang.studio.core.command.ExecuteCommand;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

@Component
@AllArgsConstructor
public class ConnectListener {

   private final Workspace workspace;
   
   public void connect(CommandListener listener, Path path) {
      String script = StudioOption.SCRIPT.getValue();
      
      if(script != null) {
         try {
            Project project = workspace.createProject(path);
            File projectPath = project.getBasePath();
            String projectName = project.getName();
            File file = new File(projectPath, "/" + script);
            FileInputStream input = new FileInputStream(file);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int count = 0;
            
            while((count = input.read(chunk))!=-1) {
               buffer.write(chunk, 0, count);
            }
            input.close();
            buffer.close();
            
            String source = buffer.toString("UTF-8");
            String system = System.getProperty("os.name");
            ExecuteCommand command = ExecuteCommand.builder()
                  .project(projectName)
                  .system(system)
                  .resource(script)
                  .source(source)
                  .breakpoints(Collections.EMPTY_MAP)
                  .build();
            
            listener.onExecute(command);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}