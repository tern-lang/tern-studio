package org.ternlang.studio.core.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.ternlang.studio.agent.core.ExecuteStatus;

import com.google.common.base.Preconditions;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeginCommand implements Command {

   private ExecuteStatus status;
   private String resource;
   private String process;
   private String system;
   private String pid;
   private long duration;
   private boolean debug;
   
   public BeginCommand validate() {
      Preconditions.checkNotNull(pid, "Process identity must not be null");
      Preconditions.checkNotNull(system, "System must not be null");
      Preconditions.checkNotNull(status, "Status must not be null");
      Preconditions.checkNotNull(process, "Process must not be null");
      Preconditions.checkNotNull(resource, "Resource must not be null");
      
      if(status.isDebug()) {
         Preconditions.checkArgument(debug, "Debug value is " + debug + " for " + status);
      }
      if(status.isStarted()) {
         Preconditions.checkArgument(resource != null, "Resource required for " + status);
      }
      return this;
   }
}