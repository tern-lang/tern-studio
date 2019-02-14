package org.ternlang.studio.index.scan;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.classpath.ClassOrigin;
import org.ternlang.studio.index.classpath.ClassCategory;

@Slf4j
class ResourceClassFile implements ClassFile {
   
   private final ClassOrigin category;
   private final ClassLoader loader;
   private final String path;
   private String fullName;
   private String absolute;
   private String location;
   private Class type;
   private URL url;
   
   public ResourceClassFile(String path, ClassLoader loader, boolean jdk) {
      this.category = jdk ? ClassOrigin.JDK : ClassOrigin.PROJECT;
      this.loader = loader;
      this.path = path;
   }

   @Override
   public ClassOrigin getOrigin() {
      return category;
   }
   
   @Override
   public ClassCategory getCategory() {
      try {
         Class type = loadClass();
         
         if(type != null) {
            if(type.isInterface()) {
               return ClassCategory.INTERFACE;
            }
            if(type.isEnum()) {
               return ClassCategory.ENUM;
            }
         }
         return ClassCategory.CLASS;
      } catch(Throwable e){
         return ClassCategory.CLASS;
      }
   }

   @Override
   public int getModifiers() {
      try{
         Class type = loadClass();
         return type.getModifiers();
      } catch(Throwable e){
         return 0;
      }
   }
   
   private URL getURL(){
      try {
         if(url == null){
            log.info("Resolving URL for " + path);
            url = loader.getResource(path);
         }
      }catch(Throwable e){
         e.printStackTrace();
         return null;
      }
      return url;
   }
   
   @Override
   public Class loadClass() {
      try {
         if(type == null) {
            String path = getFullName();
            log.info("Loading class for " + path);
            type = loader.loadClass(path);
         }
      } catch(Throwable e) {
         e.printStackTrace();
         return null;
      }
      return type;
   }
   
   @Override
   public String getFullName() {
      if(fullName == null) {
         String path = getResource();
         
         if(path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1);
         }
         int length = path.length();
         int extension = ".class".length();
         
         path = path.substring(0, length - extension);
         path = path.replace('/', '.');
         path = path.replace('\\', '.');
         
         fullName = path;
      }
      return fullName;
   }
   
   @Override
   public String getTypeName() {
      String name = getFullName();
      int index = name.lastIndexOf('.');
      
      if(index != -1) {
         return name.substring(index + 1);
      }
      return name;
   }
   
   @Override
   public String getShortName() {
      String name = getTypeName();
      int index = name.lastIndexOf('$');
      
      if(index != -1) {
         return name.substring(index + 1);
      }
      return name;
   }
   
   @Override
   public String getModule() {
      String name = getFullName();
      int index = name.lastIndexOf('.');
      
      if(index != -1) {
         return name.substring(0, index);
      }
      return name;
   }
   
   @Override
   public String getLibraryPath() {
      if(absolute == null) {
         URL url = getURL();
         String token = String.valueOf(url).toLowerCase();
         
         if(token.startsWith("jar:file")) {
            try {
               JarURLConnection connection = (JarURLConnection) url.openConnection();
               JarFile jarFile = connection.getJarFile();
               File file = new File(jarFile.getName());
               
               absolute = file.getCanonicalPath();
               return absolute;
            } catch(Throwable e) {}
         }
         absolute = getResource();
      }
      return absolute;
   }

   @Override
   public String getLibrary() {
      if(location == null) {
         URL url = getURL();
         String token = String.valueOf(url).toLowerCase();
         
         if(token.startsWith("jar:file")) {
            try {
               JarURLConnection connection = (JarURLConnection)url.openConnection();
               URL jarUrl = connection.getJarFileURL();
               File file = new File(jarUrl.toURI());
               
               location = file.getCanonicalFile().getName();
               return location;
            } catch(Throwable e) {}
         }
         location = getResource();
      }
      return location;
   }
   
   @Override
   public String getResource() {
      return path;
   }
}