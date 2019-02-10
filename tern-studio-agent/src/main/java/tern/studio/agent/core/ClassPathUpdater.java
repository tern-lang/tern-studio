package tern.studio.agent.core;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassPathUpdater {
   
   private static final String INCLUDE_MESSAGE = "Including %s in classpath";
   private static final String ADD_URL_METHOD = "addURL";
   private static final String JAR_EXTENSION = ".jar";
   
   public static List<File> parseClassPath(String dependencies) throws Exception {
      Reader source = new StringReader(dependencies);
      LineNumberReader reader = new LineNumberReader(source);
      List<File> files = new ArrayList<File>();
      
      try {
         String line = reader.readLine();
         
         while(line != null) {
            String token = line.trim();
            int length = token.length();
            
            if(length > 0) {
               if(!token.startsWith("#")) {
                  File file = new File(token);
                  files.add(file);
               }
            }
            line = reader.readLine();
         }
      } finally {
         reader.close();
      }
      return files;
   }
   
   public static ClassLoader updateClassPath(String dependencies) throws Exception {  
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      
      if(URLClassLoader.class.isInstance(loader)) { // could be Android PathClassLoader
         Method method = URLClassLoader.class.getDeclaredMethod(ADD_URL_METHOD, URL.class);
         List<File> files = parseClassPath(dependencies);
         int size = files.size();
         
         if(size > 0) {
            for(int i = 0; i < size; i++){
               File file = files.get(i);
               URI location = file.toURI();
               URL path = location.toURL();
               
               method.setAccessible(true);
               method.invoke(loader, path);
            } 
         }
         return loader;
      }
      return null;
   }
   
   public static void updateClassPath(List<File> dependencies, boolean debug) throws Exception {
      URLClassLoader loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
      Method method = URLClassLoader.class.getDeclaredMethod(ADD_URL_METHOD, URL.class);
      
      for(File dependency : dependencies) {
         String resource = dependency.getAbsolutePath();
         
         if(dependency.isFile() && !resource.endsWith(JAR_EXTENSION)) {
            FileReader source = new FileReader(dependency);
            LineNumberReader reader = new LineNumberReader(source);
            List<File> files = new ArrayList<File>();
            
            try {
               String line = reader.readLine();
               
               while(line != null) {
                  String token = line.trim();
                  int length = token.length();
                  
                  if(length > 0) {
                     File file = new File(token);
                     files.add(file);
                  }
                  line = reader.readLine();
               }
               int size = files.size();
               
               if(size > 0) {
                  for(int i = 0; i < size; i++){
                     File file = files.get(i).getCanonicalFile();
                     URI location = file.toURI();
                     URL path = location.toURL();
                     
                     if(debug) {
                        String message = String.format(INCLUDE_MESSAGE , path);
                        System.err.println(message);
                     }
                     if(!file.exists()) {
                        throw new IllegalArgumentException("Could not find " + path);
                     }
                     method.setAccessible(true);
                     method.invoke(loader, path);
                  } 
               }
            } finally {
               reader.close();
            }
         } else {
            File file = dependency.getCanonicalFile();
            URI location = file.toURI();
            URL path = location.toURL();
            
            if(debug) {
               String message = String.format(INCLUDE_MESSAGE , path);
               System.err.println(message);
            }
            if(!file.exists()) {
               throw new IllegalArgumentException("Could not find " + path);
            }
            method.setAccessible(true);
            method.invoke(loader, path);
         }
      }
   }
}
