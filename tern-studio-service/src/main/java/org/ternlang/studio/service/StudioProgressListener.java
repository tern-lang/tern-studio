package org.ternlang.studio.service;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.ternlang.studio.common.ProgressManager;
import org.ternlang.studio.resource.action.annotation.ComponentListener;

@org.ternlang.studio.resource.action.annotation.Component
@Component
public class StudioProgressListener implements ApplicationListener<ApplicationStartingEvent>, ComponentListener {

   @Override
   public void onApplicationEvent(ApplicationStartingEvent event) {
      ProgressManager.getProgress().update("Starting components");
   }

   @Override
   public void onReady() {
      ProgressManager.getProgress().update("Starting components");
   }
}
