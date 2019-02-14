package org.ternlang.studio.service.image;

import static java.awt.Image.SCALE_SMOOTH;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageScaler {

   public static BufferedImage scale(BufferedImage originalImage, int width, int height) throws Exception {
      Image scaledImage = originalImage.getScaledInstance(width, height, SCALE_SMOOTH);
      BufferedImage bufferedThumbnail = createImage(scaledImage);
      Graphics graphics = bufferedThumbnail.getGraphics();

      graphics.drawImage(scaledImage, 0, 0, null);

      return bufferedThumbnail;
   }

   public static BufferedImage scaleHeight(BufferedImage originalImage, int height) throws Exception {
      Image scaledImage = originalImage.getScaledInstance(-1, height, SCALE_SMOOTH);
      BufferedImage bufferedThumbnail = createImage(scaledImage);
      Graphics graphics = bufferedThumbnail.getGraphics();

      graphics.drawImage(scaledImage, 0, 0, null);

      return bufferedThumbnail;
   }

   public static BufferedImage scaleWidth(BufferedImage originalImage, int width) throws Exception {
      Image scaledImage = originalImage.getScaledInstance(width, -1, SCALE_SMOOTH);
      BufferedImage bufferedThumbnail = createImage(scaledImage);
      Graphics graphics = bufferedThumbnail.getGraphics();

      graphics.drawImage(scaledImage, 0, 0, null);

      return bufferedThumbnail;
   }

   private static BufferedImage createImage(Image scaledImage) {
      int width = scaledImage.getWidth(null);
      int height = scaledImage.getHeight(null);

      return new BufferedImage(width, height, TYPE_INT_ARGB);
   }
}