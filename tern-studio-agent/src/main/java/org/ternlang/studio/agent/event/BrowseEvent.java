package org.ternlang.studio.agent.event;

import java.util.Collections;
import java.util.Set;

public class BrowseEvent implements ProcessEvent {

   private final Set<String> expand;
   private final String process;
   private final String thread;
   
   private BrowseEvent(Builder builder) {
      this.expand = Collections.unmodifiableSet(builder.expand);
      this.process = builder.process;
      this.thread = builder.thread;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public Set<String> getExpand() {
      return expand;
   }
   
   public String getThread() {
      return thread;
   }

   public static class Builder {
      
      private Set<String> expand;
      private String process;
      private String thread;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withExpand(Set<String> expand) {
         this.expand = expand;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
      }
      
      public BrowseEvent build(){
         return new BrowseEvent(this);
      }
   }
}