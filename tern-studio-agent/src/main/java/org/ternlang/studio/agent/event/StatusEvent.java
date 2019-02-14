package org.ternlang.studio.agent.event;

import org.ternlang.studio.agent.core.ExecuteStatus;

public interface StatusEvent extends ProcessEvent {
   ExecuteStatus getStatus();
   String getProject();
   String getSystem();
   String getResource();
   String getPid();
   long getUsedMemory();
   long getTotalMemory();
   int getThreads();

   public static interface Builder<T extends StatusEvent> {
      Builder<T> withProject(String project);
      Builder<T> withStatus(ExecuteStatus status);
      Builder<T> withResource(String resource);
      Builder<T> withSystem(String system);
      Builder<T> withPid(String pid);
      Builder<T> withThreads(int threads);
      Builder<T> withTotalMemory(long totalMemory);
      Builder<T> withUsedMemory(long usedMemory);
      T build();
   }
}
