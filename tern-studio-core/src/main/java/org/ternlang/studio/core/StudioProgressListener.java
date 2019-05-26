package org.ternlang.studio.core;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.core.ComponentListener;
import org.ternlang.studio.common.ProgressManager;

@Component
public class StudioProgressListener implements ComponentListener {

   @Override
   public void onReady() {
      ProgressManager.getProgress().update("Starting components");
   }
}
