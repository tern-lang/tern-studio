package org.ternlang.studio.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

   public static void extract(File from, File to) throws IOException {
      extract(new FileInputStream(from), to);
   }

   public static void extract(InputStream from, File to) throws IOException {
      File parent = to.getParentFile();
      if (!parent.exists()) {
         parent.mkdir();
      }
      byte[] buffer = new byte[1024 * 10];
      ZipInputStream zis = new ZipInputStream(from);
      ZipEntry ze = zis.getNextEntry();

      while (ze != null) {
         if (ze.isDirectory()) {
            ze = zis.getNextEntry();
            continue;
         }
         String fileName = ze.getName();
         File newFile = new File(to, fileName);
         File parentDir = newFile.getParentFile();
         
         parentDir.mkdirs();

         FileOutputStream fos = new FileOutputStream(newFile);
         try {
            int count = 0;
            while ((count = zis.read(buffer)) > 0) {
               fos.write(buffer, 0, count);
            }
         } finally {
            fos.close();
         }
         ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();
   }

}
