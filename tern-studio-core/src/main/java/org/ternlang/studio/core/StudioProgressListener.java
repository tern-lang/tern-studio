package org.ternlang.studio.core;

import org.ternlang.service.annotation.Component;
import org.ternlang.service.annotation.ComponentListener;
import org.ternlang.studio.common.ProgressManager;

@Component
public class StudioProgressListener implements ComponentListener {

   @Override
   public void onReady() {
      ProgressManager.getProgress().update("Starting components");
   }
}
