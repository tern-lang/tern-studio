package tern.studio.common.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import org.springframework.stereotype.Component;

@Component
public class FileManager {
   
   private static final String DEFAULT_RESOURCE_PATH = "/resource";
   private static final String DEFAULT_ENCODING = "UTF-8";

   private final String encoding;
   private final String base;

   public FileManager() {
      this(DEFAULT_RESOURCE_PATH, DEFAULT_ENCODING);
   }

   public FileManager(String base, String encoding) {
      this.encoding = encoding;
      this.base = base;
   }

   public Content getContent(String path) throws IOException {
      URL resource = getResource(path);
      
      if(resource != null) {
         try {
            URI location = resource.toURI();
            File file = new File(location);
            
            if(file.exists()) {
               return new FileContent(path, file, resource, encoding);
            }
         }catch(Exception e) {
            return new ClassPathContent(path, resource, encoding);
         }
         return new ClassPathContent(path, resource, encoding);
      }
      return null;
   }
   
   private URL getResource(String path) {
      String root = base;
      
      if(!root.startsWith("/")) {
         root = "/" + root;
      }
      if(path.startsWith("/")) {
         path = path.substring(1);
      }
      if(root.endsWith("/")) {
         return FileManager.class.getResource(root +path);
      }
      return FileManager.class.getResource(root + "/" +path);
   }
   
   private static class FileContent implements Content {
      
      private final String encoding;
      private final URL resource;
      private final String path;
      private final File file;
      
      public FileContent(String path, File file, URL resource, String encoding) {
         this.resource = resource;
         this.encoding = encoding;
         this.path = path;
         this.file = file;
      }

      @Override
      public InputStream getInputStream() {
         try {
            return resource.openStream();
         } catch(Exception e) {
            throw new IllegalStateException("Error opening resource " + path, e);
         }
      }
      
      @Override
      public Reader getReader() {
         try {
            InputStream stream = getInputStream();
            return new InputStreamReader(stream, encoding);
         } catch(Exception e) {
            throw new IllegalStateException("Error opening resource " + path, e);
         }
      }

      @Override
      public String getPath() {
         return path;
      }

      @Override
      public long getModificationTime() {
         return file.lastModified();
      }

      @Override
      public boolean isLocalFile() {
         return file.exists();
      }
   }
   
   private static class ClassPathContent implements Content {
      
      private final String encoding;
      private final URL resource;
      private final String path;
      
      public ClassPathContent(String path, URL resource, String encoding) {
         this.resource = resource;
         this.encoding = encoding;
         this.path = path;
      }

      @Override
      public InputStream getInputStream() {
         try {
            return resource.openStream();
         } catch(Exception e) {
            throw new IllegalStateException("Error opening resource " + path, e);
         }
      }
      
      @Override
      public Reader getReader() {
         try {
            InputStream stream = getInputStream();
            return new InputStreamReader(stream, encoding);
         } catch(Exception e) {
            throw new IllegalStateException("Error opening resource " + path, e);
         }
      }

      @Override
      public String getPath() {
         return path;
      }

      @Override
      public long getModificationTime() {
         return Long.MAX_VALUE;
      }

      @Override
      public boolean isLocalFile() {
         return false;
      }
   }
}