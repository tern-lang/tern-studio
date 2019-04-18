package org.ternlang.studio.service.pacman;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class ImageLoader {

   public static Image loadImage(String path) {
      URL url = ClipManager.class.getClassLoader().getResource(path);
      return Toolkit.getDefaultToolkit().getImage(url);
   }
}
