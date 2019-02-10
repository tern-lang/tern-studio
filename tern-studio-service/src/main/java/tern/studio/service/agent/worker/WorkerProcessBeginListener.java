package tern.studio.service.agent.worker;

import tern.studio.agent.ProcessMode;
import tern.studio.agent.event.BeginEvent;
import tern.studio.agent.event.ExitEvent;
import tern.studio.agent.event.ProcessEventAdapter;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.service.ProcessRemoteController;

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