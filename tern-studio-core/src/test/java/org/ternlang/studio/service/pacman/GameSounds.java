package org.ternlang.studio.service.pacman;

import javax.sound.sampled.Clip;

/* This class controls all sound effects*/
public class GameSounds {

   private Clip nomNom;
   private Clip newGame;
   private Clip death;
   /* Keeps track of whether or not the eating sound is playing */
   private boolean stopped;

   /* Initialize audio files */
   public GameSounds() {
      this.nomNom = ClipManager.loadClip("sounds/nomnom.wav");
      this.newGame = ClipManager.loadClip("sounds/newGame.wav");
      this.death = ClipManager.loadClip("sounds/death.wav");
      this.stopped = true;
   }

   /* Play pacman eating sound */
   public void nomNom() {
      /* If it's already playing, don't start it playing again! */
      if (!stopped)
         return;

      stopped = false;
      nomNom.stop();
      nomNom.setFramePosition(0);
      nomNom.loop(Clip.LOOP_CONTINUOUSLY);
   }

   /* Stop pacman eating sound */
   public void nomNomStop() {
      stopped = true;
      nomNom.stop();
      nomNom.setFramePosition(0);
   }

   /* Play new game sound */
   public void newGame() {
      newGame.stop();
      newGame.setFramePosition(0);
      newGame.start();
   }

   /* Play pacman death sound */
   public void death() {
      death.stop();
      death.setFramePosition(0);
      death.start();
   }
}
