package org.ternlang.studio.agent.core;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassPathUpdater {
   
   private static final String INCLUDE_MESSAGE = "Including %s in classpath";
   private static final String URL_CLASS_PATH_FIELD = "ucp";
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
      
      if(SecureClassLoader.class.isInstance(loader)) { // could be Android PathClassLoader
         URLClassPath path = createClassPath(loader);
         List<File> files = parseClassPath(dependencies);
         int size = files.size();
         
         if(size > 0) {
            for(int i = 0; i < size; i++){
               File file = files.get(i);
               URI location = file.toURI();
               URL entry = location.toURL();
               
               path.addURL(entry);
            } 
         }
         return loader;
      }
      return null;
   }

   public static ClassLoader createClassLoader(List<File> dependencies, boolean debug) throws Exception {
      URLClassLoader loader = new URLClassLoader(new URL[]{}, null);
      updateClassLoader(dependencies, loader, debug);
      return loader;
   }

   public static void updateClassPath(List<File> dependencies, boolean debug) throws Exception {
      ClassLoader loader = (ClassLoader)ClassLoader.getSystemClassLoader();
      updateClassLoader(dependencies, loader, debug);
   }

   private static void updateClassLoader(List<File> dependencies, ClassLoader loader, boolean debug) throws Exception {
      URLClassPath path = createClassPath(loader);

      for(File dependency : dependencies) {
         String resource = dependency.getAbsolutePath();

         if(dependency.isFile() && !resource.endsWith(JAR_EXTENSION)) {
            FileReader reader = new FileReader(dependency);
            LineNumberReader iterator = new LineNumberReader(reader);
            List<File> files = new ArrayList<File>();

            try {
               String line = iterator.readLine();

               while(line != null) {
                  String token = line.trim();
                  int length = token.length();

                  if(length > 0) {
                     File file = new File(token);
                     files.add(file);
                  }
                  line = iterator.readLine();
               }
               int size = files.size();

               if(size > 0) {
                  for(int i = 0; i < size; i++){
                     File file = files.get(i).getCanonicalFile();
                     URI location = file.toURI();
                     URL entry = location.toURL();

                     if(debug) {
                        String message = String.format(INCLUDE_MESSAGE , entry);
                        System.err.println(message);
                     }
                     if(!file.exists()) {
                        throw new IllegalArgumentException("Could not find " + path);
                     }
                     path.addURL(entry);
                  }
               }
            } finally {
               reader.close();
            }
         } else {
            File file = dependency.getCanonicalFile();
            URI location = file.toURI();
            URL entry = location.toURL();

            if(debug) {
               String message = String.format(INCLUDE_MESSAGE , entry);
               System.err.println(message);
            }
            if(!file.exists()) {
               throw new IllegalArgumentException("Could not find " + path);
            }
            path.addURL(entry);
         }
      }
   }
   
   private static URLClassPath createClassPath(ClassLoader loader) throws Exception {
      Class base = loader.getClass();
      Class root = base;
      
      while(base != null) {
         try {
            Field[] fields = base.getDeclaredFields();
            
            for(Field field : fields) {   
               String name = field.getName();
            
               if(name.equals(URL_CLASS_PATH_FIELD)) {
                  field.setAccessible(true); // make URLClassPath available Java 9+ does not like this

                  Object source = field.get(loader);
                  Class type = source.getClass();
                  Method method = type.getDeclaredMethod(ADD_URL_METHOD, URL.class);
                  
                  method.setAccessible(true);
                  
                  return new URLClassPath(source, method);
               }
            }
         } catch(Throwable e) {
            e.printStackTrace();
         }
         base = base.getSuperclass();
      }
      throw new IllegalStateException("No such field " + URL_CLASS_PATH_FIELD + " in " + root);
   }

   
   private static class URLClassPath {
      
      private final Method method;
      private final Object source;
      
      public URLClassPath(Object source, Method method) {
         this.source = source;
         this.method = method;
      }
      
      public void addURL(URL url) {
         try {
            method.setAccessible(true);
            method.invoke(source, url);
         }catch(Throwable e) {
            throw new IllegalStateException("Could not add " + url + " to class path");
         }
      }
   }
}
