package org.ternlang.studio.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgressManager {
   
   private static ProgressListener PROGRESS_LISTENER = new ProgressListener() {
      
      @Override
      public void update(String message) {
         log.info("Progress update: " + message);
      }
   };

   public static ProgressListener getProgress() {
      return PROGRESS_LISTENER;
   }
   
   public static void setProgress(ProgressListener listener) {
      if(listener != null) {
         PROGRESS_LISTENER = listener;
      }
   }
}
