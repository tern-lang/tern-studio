package org.ternlang.studio.core.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.resource.Content;
import org.simpleframework.module.resource.ContentTypeResolver;
import org.simpleframework.module.resource.FileResolver;
import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;

@Component
public class ImageScaleService {
   
   private static final int SCALE_HEIGHT = 40;

   private final Cache<String, ScaledImage> scaledImages;
   private final ContentTypeResolver typeResolver;
   private final FileResolver fileResolver;

   public ImageScaleService(ContentTypeResolver typeResolver, FileResolver fileResolver) {
      this.scaledImages = new LeastRecentlyUsedCache<String, ScaledImage>(200);
      this.typeResolver = typeResolver;
      this.fileResolver = fileResolver;
   }
   
   public ScaledImage getImage(String path) throws Exception {
      ScaledImage image = scaledImages.fetch(path);
      
      if(image == null) {
         image = getScaledImage(path);
         scaledImages.cache(path, image);
      }
      return image;
   }
   
   private ScaledImage getScaledImage(String path) throws Exception {
      String contentType = typeResolver.resolveType(path);
      ImageType imageType = ImageType.resolveByType(contentType);
      ImageWriter imageWriter = imageType.getImageWriter();
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      imageWriter.setOutput(ImageIO.createImageOutputStream(output));

      BufferedImage originalImage = getOriginalImage(path);
      BufferedImage scaledImage = ImageScaler.scaleHeight(originalImage, SCALE_HEIGHT);
      
      imageWriter.write(scaledImage);
      output.close();
      
      byte[] data = output.toByteArray();
      int height = scaledImage.getHeight();
      int width = scaledImage.getWidth();
      
      return new ScaledImage(contentType, data, width, height);
   }
   
   private BufferedImage getOriginalImage(String path) throws Exception {
      Content content = fileResolver.resolveContent(path);
      
      if(content == null) {
         throw new IOException("Could not get original image " + path);
      }
      InputStream contentStream = content.getInputStream();
      
      try {
         String contentType = typeResolver.resolveType(path);
         ImageType imageType = ImageType.resolveByType(contentType);
         ImageReader imageReader = imageType.getImageReader();
         ImageInputStream imageStream = ImageIO.createImageInputStream(contentStream);
         
         imageReader.setInput(imageStream);
   
         return imageReader.read(0);
      }finally {
         contentStream.close();
      }
   }
   
   public static class ScaledImage {
      
      private final int height;
      private final int width;
      private final String type;
      private final byte[] data;
      
      public ScaledImage(String type, byte[] data, int width, int height){
         this.width = width;
         this.height = height;
         this.type = type;
         this.data = data;
      }
      
      public int getWidth(){
         return width;
      }
      
      public int getHeight(){
         return height;
      }
      
      public byte[] getData(){
         return data;
      }
      
      public String getType(){
         return type;
      }
   }
}