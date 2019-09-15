package org.ternlang.studio.agent.limit;

public class TimeLimit {

   private final long expiryTime;
   private final long timeout;
   
   public TimeLimit(long expiryTime, long timeout) {
      this.expiryTime = expiryTime;
      this.timeout = timeout;
   }

   public long getExpiryTime() {
      return expiryTime;
   }

   public long getTimeout() {
      return timeout;
   }
}
