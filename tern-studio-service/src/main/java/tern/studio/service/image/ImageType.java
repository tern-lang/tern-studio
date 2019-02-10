package tern.studio.service.image;

import static javax.imageio.ImageIO.getImageReadersByMIMEType;
import static javax.imageio.ImageIO.getImageWritersByMIMEType;

import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

public enum ImageType {
   PNG("image/png", "png", "image/png"),
   GIF("image/gif", "gif", "image/gif"),
   JPEG("image/jpeg", "jpg", "jpeg", "image/jpeg");

   private final List<String> terms;
   private final String type;

   private ImageType(String type, String... terms) {
      this.terms = Arrays.asList(terms);
      this.type = type;
   }

   public String getType() {
      return type;
   }

   public ImageReader getImageReader() {
      return getImageReadersByMIMEType(type).next();
   }

   public ImageWriter getImageWriter() {
      return getImageWritersByMIMEType(type).next();
   }

   public static ImageType resolve(String fileName, String type) {
      if (type == null) {
         return resolveByFileName(fileName);
      }
      return resolveByType(type);
   }

   public static ImageType resolveByFileName(String fileName) {
      if (fileName != null) {
         String token = fileName.toLowerCase();

         for (ImageType imageType : values()) {
            for (String term : imageType.terms) {
               if (token.endsWith(term)) {
                  return imageType;
               }
            }
         }
      }
      return PNG;
   }

   public static ImageType resolveByType(String type) {
      if (type != null) {
         String token = type.toLowerCase();

         for (ImageType imageType : values()) {
            if (imageType.terms.contains(token)) {
               return imageType;
            }
         }
      }
      return PNG;
   }
}