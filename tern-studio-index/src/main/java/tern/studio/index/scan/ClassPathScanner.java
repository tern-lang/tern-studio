package tern.studio.index.scan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import tern.studio.index.classpath.ClassFile;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassPathScanner {
   
   private static final List<ClassFile> BOOTSTRAP_CLASSES = new ArrayList<ClassFile>();
   private static final String[] RESOURCE_EXTENSIONS = {
      ".java",
      ".class"
   };
   private static final String[] RUNTIME_JAR_PATHS = {
      "/jre/lib/rt.jar",
      "/lib/rt.jar"
   };
   private static final String[] TARGET_PATHS = {
      "target/classes/",
      "target/test-classes/"
   };
   
   static {
      scanBootstrapClassPath();
   }
   
   public static List<ClassFile> scanAllClasses(ClassLoader loader) {
      List<ClassFile> files = new ArrayList<ClassFile>();
      
      files.addAll(scanBootstrapClassPath());
      files.addAll(scanClassLoader(loader));
      
      return files;
   }
   
   public static List<ClassFile> scanBootstrapClassPath() {
      if(BOOTSTRAP_CLASSES.isEmpty()) {
         try {
            String javaHome = System.getProperty("java.home");
            
            for(String path : RUNTIME_JAR_PATHS) {
               File file = new File(javaHome, path);
               
               if(file.exists()) {
                  String location = file.getCanonicalPath();
                  findClassesInJar(BOOTSTRAP_CLASSES, location, true);
                  break;
               }
            }
         } catch (Throwable e) {
            return Collections.emptyList();
         }
      }
      return Collections.unmodifiableList(BOOTSTRAP_CLASSES);
   }
   
   public static List<ClassFile> scanClassLoader(ClassLoader loader) {
      try {
         Set<ClassInfo> projectClasses = ClassPath.from(loader).getAllClasses();
         
         if(!projectClasses.isEmpty()) {
            List<ClassFile> files = new ArrayList<ClassFile>();
         
            for(ClassInfo info : projectClasses) {
               String path = info.getResourceName();
               
               if(!isTargetResource(path)) {
                  String resource = info.getResourceName();
                  ClassFile file = new ResourceClassFile(resource, loader, false);
                  files.add(file);
               }
            }
            return Collections.unmodifiableList(files);
         }
      } catch(Throwable e) {
         return Collections.emptyList();
      }
      return Collections.emptyList();
   }
   
   private static boolean isTargetResource(String resourcePath) {
      for(String targetPath : TARGET_PATHS) {
         if(resourcePath.contains(targetPath)){
            return true;
         }
      }
      return false;
   }
   
   private static void findClassesInJar(List<ClassFile> classFiles, String path, boolean jdk) throws Exception {
      JarFile jarFile = new JarFile(path);

      try {
         Enumeration<JarEntry> entries = jarFile.entries();

         while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String location = entry.getName();
            ClassFile file = createClassFile(location, jdk);
            
            if (file != null) {
               classFiles.add(file);
            }
         }
      } finally {
         jarFile.close();
      }
   }

   public static ClassFile createClassFile(String path) throws Exception {
      return createClassFile(path, false);
   }
   
   public static ClassFile createClassFile(String path, boolean jdk) throws Exception {
      for(String extension : RESOURCE_EXTENSIONS) {
         if (path.endsWith(extension)) {            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return new ResourceClassFile(path, loader, jdk);
         }
      }
      return null;
   }
}
