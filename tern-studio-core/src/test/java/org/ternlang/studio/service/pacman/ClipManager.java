package org.ternlang.studio.service.pacman;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ClipManager {

   public static Clip loadClip(String path) {
      try {
         URL url = ClipManager.class.getClassLoader().getResource(path);
         AudioInputStream stream = AudioSystem.getAudioInputStream(url);
         Clip clip = AudioSystem.getClip();
         clip.open(stream);
         return clip;
      } catch(Exception e) {
         throw new IllegalStateException("Could not load sound " + path, e);
      }
   }
}
