package org.ternlang.studio.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.simpleframework.module.common.ClassPathReader;

public class FilePersister {

   public static void writeAsString(File rootPath, String path, String resource) throws Exception {
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(rootPath, realPath);
      writeAsString(sourceFile, resource);
   }
   
   public static void writeAsString(File filePath, String resource) throws Exception {
      byte[] octets = resource.getBytes("UTF-8");
      writeAsByteArray(filePath, octets);
   }
   
   public static void writeAsByteArray(File rootPath, String path, byte[] resource) throws Exception {
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(rootPath, realPath);
      writeAsByteArray(sourceFile, resource);
   }
   
   public static void writeAsByteArray(File filePath, byte[] resource) throws Exception {
      File parentFile = filePath.getParentFile();
      
      if(!parentFile.exists()) {
         parentFile.mkdirs();
      }
      FileOutputStream outputStream = new FileOutputStream(filePath);
      outputStream.write(resource);
      outputStream.close();
   }
   
   public static String readAsString(File rootPath, String path) throws Exception {
      byte[] resource = readAsByteArray(rootPath, path);
      return new String(resource, "UTF-8");
   }
   
   public static String readAsString(File rootPath) throws Exception {
      byte[] resource = readAsByteArray(rootPath);
      return new String(resource, "UTF-8");
   }
   
   public static byte[] readAsByteArray(File rootPath, String path) throws Exception {
      String realPath = path.replace('/', File.separatorChar);
      File projectFile = new File(rootPath, realPath);
      byte[] octets = readAsByteArray(projectFile);
      
      if(octets == null) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         InputStream inputStream = ClassPathReader.class.getResourceAsStream(path);
         byte[] chunk = new byte[8192];
         int count = 0;

         while((count = inputStream.read(chunk)) != -1) {
            buffer.write(chunk, 0, count);
         }
         inputStream.close();
         return buffer.toByteArray();
      }   
      return octets;
   }
      
   public static byte[] readAsByteArray(File filePath) throws Exception {
      if(!filePath.exists()) {
         return null;
      }
      long length = filePath.length();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream((int)length);
      InputStream inputStream = new FileInputStream(filePath);
      byte[] chunk = new byte[8192];
      int count = 0;

      while((count = inputStream.read(chunk)) != -1) {
         buffer.write(chunk, 0, count);
      }
      inputStream.close();
      return buffer.toByteArray();
   }
}
