package tern.studio.service;

import tern.studio.common.ProgressManager;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StudioProgressListener implements ApplicationListener<ApplicationStartingEvent> {

   @Override
   public void onApplicationEvent(ApplicationStartingEvent event) {
      ProgressManager.getProgress().update("Starting components");
   }

}
