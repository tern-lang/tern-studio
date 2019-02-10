package tern.studio.service;

import tern.studio.common.ProgressListener;

public interface SplashPanel extends ProgressListener {   
   void show(long duration);
   void dispose();
}
