package org.ternlang.studio.core.command;

import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.agent.message.event.ProcessOrigin;
import org.ternlang.studio.agent.event.ProcessEventFilter;

public class CommandFilter implements ProcessEventFilter {

   private final AtomicReference<String> attachment;
   private final String session;

   public CommandFilter(String session) {
      this.attachment = new AtomicReference<String>();
      this.session = session;
   }

   public String getSession(){
      return session;
   }
   
   @Override
   public String getFocus(){
      return attachment.get();
   }

   @Override
   public void setFocus(String process) {
      attachment.set(process);
   }
   
   public boolean isFocused(ProcessOrigin origin) {
      String process = origin.process().toString();
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