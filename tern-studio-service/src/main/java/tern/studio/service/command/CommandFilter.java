package tern.studio.service.command;

import java.util.concurrent.atomic.AtomicReference;

import tern.studio.agent.event.ProcessEvent;
import tern.studio.agent.event.ProcessEventFilter;

public class CommandFilter implements ProcessEventFilter {

   private final AtomicReference<String> attachment;
   
   public CommandFilter() {
      this.attachment = new AtomicReference<String>();
   }
   
   @Override
   public String getFocus(){
      return attachment.get();
   }

   @Override
   public void setFocus(String process) {
      attachment.set(process);
   }
   
   public boolean isFocused(ProcessEvent event) {
      String process = event.getProcess();
      String focus = attachment.get();
      
      if(focus != null) {
         return process.equals(focus);
      }
      return false;
   }
   
   public void clearFocus() {
      attachment.set(null);
   }

}