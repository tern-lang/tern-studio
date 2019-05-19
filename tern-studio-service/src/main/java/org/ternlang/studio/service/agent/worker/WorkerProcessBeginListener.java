package org.ternlang.studio.service.agent.worker;

import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.event.BeginEvent;
import org.ternlang.studio.agent.event.ExitEvent;
import org.ternlang.studio.agent.event.ProcessEventAdapter;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.service.ProcessRemoteController;

@Component
public class WorkerProcessBeginListener extends ProcessEventAdapter {
   
   private final ProcessRemoteController controller;
   
   public WorkerProcessBeginListener(ProcessRemoteController controller) {
      this.controller = controller;
   }
   
   @Override
   public void onBegin(ProcessEventChannel channel, BeginEvent event) {
      String process = event.getProcess();
      controller.start(process);
   }

   @Override
   public void onExit(ProcessEventChannel channel, ExitEvent event) {
      String process = event.getProcess();
      ProcessMode mode = event.getMode();
      
      if(mode.isTerminateRequired()) {
         controller.stop(process);
      }
      if(mode.isDetachRequired()) {
         controller.detach(process);
      }
   }
}