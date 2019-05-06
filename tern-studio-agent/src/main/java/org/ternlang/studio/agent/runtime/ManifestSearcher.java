package org.ternlang.studio.agent.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.jar.Manifest;

public class ManifestSearcher {

   public static final String CLASS_PATH_PROPERTY = "java.class.path";
   public static final String JAR_ENTRY = ".jar";

   public static List<Callable<Manifest>> getSearchPath() {
      List<Callable<Manifest>> sources = new ArrayList<Callable<Manifest>>();
      CallerSource caller = new CallerSource();
      ThreadContextSource threadContext = new ThreadContextSource();

      sources.add(caller);
      sources.add(threadContext);

      String classPath = System.getProperty(CLASS_PATH_PROPERTY, "");
      String[] resources = classPath.split(File.pathSeparator);

      for(String resource : resources) {
         if(resource.endsWith(JAR_ENTRY)) {
            File file = new File(resource);
            ClassPathSource path = new ClassPathSource(resource);

            if (file.exists()) {
               sources.add(path);
            }
         }
      }
      return sources;
   }

   private static class ClassPathSource implements Callable<Manifest> {

      private final String jarFile;

      public ClassPathSource(String jarFile) {
         this.jarFile = jarFile;
      }

      public Manifest call() {
         try {
            File file = new File(jarFile);

            if(file.exists()) {
               return ManifestExtractor.extractFromJar(jarFile);
            }
            return null;
         } catch(Exception e) {
            return null;
         }
      }
   }

   private static class CallerSource implements Callable<Manifest> {

      public Manifest call() {
         try {
            ClassLoader loader = getClass().getClassLoader();
            return ManifestExtractor.extractFromLoader(loader);
         } catch (Exception e) {
            return null;
         }
      }
   }

   private static class ThreadContextSource implements Callable<Manifest> {

      public Manifest call() {
         try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return ManifestExtractor.extractFromLoader(loader);
         } catch (Exception e) {
            return null;
         }
      }
   }
}
