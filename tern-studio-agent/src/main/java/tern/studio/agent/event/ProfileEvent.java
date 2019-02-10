package tern.studio.agent.event;

import java.util.Collections;
import java.util.Set;

import tern.studio.agent.profiler.ProfileResult;

public class ProfileEvent implements ProcessEvent {

   private final Set<ProfileResult> results;
   private final String process;
   
   private ProfileEvent(Builder builder) {
      this.results = Collections.unmodifiableSet(builder.results);
      this.process = builder.process;
   }

   @Override
   public String getProcess() {
      return process;
   }
   
   public Set<ProfileResult> getResults() {
      return results;
   }
   
   public static class Builder {
      
      private Set<ProfileResult> results;
      private String process;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withResults(Set<ProfileResult> results) {
         this.results = results;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public ProfileEvent build(){
         return new ProfileEvent(this);
      }
   }
}