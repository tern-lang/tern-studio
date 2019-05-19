package org.ternlang.studio.core;

import org.ternlang.studio.common.ProgressListener;

public interface SplashPanel extends ProgressListener {   
   void show(long duration);
   void dispose();
}
