package tern.studio.agent.core;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static tern.studio.agent.core.ExecuteStatus.STARTING;
import static tern.studio.agent.core.ExecuteStatus.WAITING;
import static tern.studio.agent.runtime.RuntimeAttribute.OS;
import static tern.studio.agent.runtime.RuntimeAttribute.PID;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import tern.common.LockProgress;
import tern.common.Progress;
import tern.studio.agent.runtime.RuntimeAttribute;
import tern.studio.agent.runtime.RuntimeValue;
import tern.studio.agent.runtime.RuntimeState;

public class ExecuteLatch {
   
   private static final long DEFAULT_DURATION = 24 * 60 * 60 * 1000;

   private final AtomicReference<ExecuteStatus> statusReference;
   private final AtomicReference<ExecuteData> executeReference;
   private final Progress<ExecuteStatus> progress;
   private final AtomicLong lastUpdate;
   private final ExecuteData waitData;
   private final ExecuteState state;
   private final long duration;
   
   public ExecuteLatch(String process) {
      this(process, DEFAULT_DURATION);
   }
   
   public ExecuteLatch(String process, long duration) {
      this.waitData = new ExecuteData(process, null, null, null, false);
      this.executeReference = new AtomicReference<ExecuteData>(waitData);      
      this.statusReference = new AtomicReference<ExecuteStatus>(WAITING);
      this.progress = new LockProgress<ExecuteStatus>();
      this.state = new StateReference(this, process);
      this.lastUpdate = new AtomicLong();
      this.duration = duration;
   }

   public ExecuteState getState() {
      return state;
   }
   
   public boolean start(ExecuteData startData) {
      ExecuteStatus current = statusReference.get();
      
      if(current.isTransitionForward(STARTING)) {
         long thisTime = System.nanoTime();
         
         executeReference.set(startData);
         lastUpdate.set(thisTime);
         statusReference.set(STARTING);
         progress.done(STARTING);
         return true;         
      }      
      return false;
   }
   
   public long update(ExecuteStatus status) {
      ExecuteStatus current = statusReference.get();
      
      if(current.isTransitionForward(status)) {
         long thisTime = System.nanoTime();
         long previousTime = lastUpdate.get();
         long duration = NANOSECONDS.toMillis(thisTime - previousTime);         
         
         lastUpdate.set(thisTime);
         statusReference.set(status);
         progress.done(status);    
           
         return duration;
      }
      return -1;      
   }
   
   public boolean done(ExecuteStatus status) {
      ExecuteStatus current = statusReference.get();      
      return !current.isTransitionForward(status);
   }
   
   public void wait(ExecuteStatus status) {
      wait(status, duration);
   }
   
   public void wait(ExecuteStatus status, long duration) {
      ExecuteStatus current = statusReference.get();
      
      if(current.isTransitionForward(status)) { // if not possible then its already done
         if(duration > 0) {
            progress.wait(status, duration);
         }
      }
   }  
   
   public void disconnect() {
      statusReference.getAndSet(WAITING);
      executeReference.set(waitData);     
   }
   
   private static class StateReference implements ExecuteState {
   
      private final ExecuteLatch latch;
      private final String process;
      
      public StateReference(ExecuteLatch latch, String process) {
         this.process = process;
         this.latch = latch;
      }
      
      @Override
      public ExecuteData getData(){
         return latch.executeReference.get();
      }

      @Override
      public ExecuteStatus getStatus(){
         return latch.statusReference.get();
      }

      @Override
      public String getSystem(){
         return OS.getValue();
      }

      @Override
      public String getPid(){
         return PID.getValue();
      }

      @Override
      public String getProcess(){
         return process;
      }
   }

}
