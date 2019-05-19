package org.ternlang.studio.service;

import org.ternlang.studio.common.ProgressManager;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.resource.action.annotation.ComponentListener;

@Component
public class StudioProgressListener implements ComponentListener {

   @Override
   public void onReady() {
      ProgressManager.getProgress().update("Starting components");
   }
}
