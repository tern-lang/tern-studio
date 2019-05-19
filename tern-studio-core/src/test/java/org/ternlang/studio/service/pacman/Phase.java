package org.ternlang.studio.service.pacman;

public enum Phase {
   MENU,
   START,
   STARTING,
   PLAY,
   PLAYING,
   GAME_OVER,
   WIN,
   DEMO;
   
   public boolean isPaused() {
      return this == MENU || this == WIN || this == DEMO || this == GAME_OVER;
   }
}
