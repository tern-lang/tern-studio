package org.ternlang.studio.core.agent.worker;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.transport.Channel;
import org.ternlang.agent.message.event.BeginEvent;
import org.ternlang.agent.message.event.ExitEvent;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.event.ProcessEventAdapter;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.core.ProcessManager;
import org.ternlang.studio.core.ProcessRemoteController;

@Component
public class WorkerProcessSubscriber {

   private final ProcessRemoteListener listener;
   private final ProcessManager manager;
   
   public WorkerProcessSubscriber(ProcessManager manager) {
      this.listener = new ProcessRemoteListener(manager);
      this.manager = manager;
   }

   public void subscribe(Channel channel, String process) {
      manager.connect(listener, channel, process); // establish the connection
   }
   
   private static class ProcessRemoteListener extends ProcessEventAdapter {
   
      private final ProcessRemoteController controller;
      
      public ProcessRemoteListener(ProcessRemoteController controller) {
         this.controller = controller;
      }
      
      @Override
      public void onBegin(ProcessEventChannel channel, BeginEvent event) {
         String process = event.process().toString();
         controller.start(process);
      }
   
      @Override
      public void onExit(ProcessEventChannel channel, ExitEvent event) {
         String process = event.process().toString();
         ProcessMode mode = ProcessMode.valueOf(event.mode().name());
         
         if(mode.isTerminateRequired()) {
            controller.stop(process);
         }
         if(mode.isDetachRequired()) {
            controller.detach(process);
         }
      }
   }
}