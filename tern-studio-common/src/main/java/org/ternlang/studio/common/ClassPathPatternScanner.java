package org.ternlang.studio.common;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassPathPatternScanner {
   
   private static final String CLASS_PATH_PROPERTY = "java.class.path";
   
   public static Iterator<URL> scan(String expression) throws Exception{
      int threads = Runtime.getRuntime().availableProcessors();
      String classpath = System.getProperty(CLASS_PATH_PROPERTY, "");
      
      if(classpath != null) {
         String[] paths = classpath.split(File.pathSeparator);
         Pattern pattern = FilePatternScanner.compile(expression, false);
         ExecutorService executor = Executors.newFixedThreadPool(threads);
         BlockingQueue<URL> queue = new LinkedBlockingQueue<URL>();
         
         for(String path : paths) {
            File file = new File(path);
            
            if(file.isFile()) {
               if(file.getName().endsWith(".jar")) {
                  JarFileSearcher extractor = new JarFileSearcher(queue, pattern, file);
                  executor.execute(extractor);
               } else {
                  URL entry = file.toURI().toURL();
                  queue.offer(entry);
               }
            } else if(file.isDirectory()) {
               FilePatternScanner.scan(expression, file)
                  .getFiles()
                  .stream()
                  .filter(File::isFile)
                  .map(entry -> {
                     try {
                        return entry.toURI().toURL();
                     } catch(Exception e) {
                        throw new IllegalStateException("Could not convert file " + entry, e);
                     }
                  })
                  .forEach(queue::offer);
            }
         }
         executor.shutdown();
         executor.awaitTermination(1, TimeUnit.MINUTES);
         
         return queue.iterator();
      }
      return Collections.emptyIterator();
   }
   
   private static class JarFileSearcher implements Runnable {
      
      private final BlockingQueue<URL> queue;
      private final Pattern pattern;
      private final File file;
      
      public JarFileSearcher(BlockingQueue<URL> queue, Pattern pattern, File file) {
         this.pattern = pattern;
         this.queue = queue;
         this.file = file;
      }
      
      @Override
      public void run() {
         try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            JarFile jarFile = new JarFile(file); 
            Enumeration<? extends JarEntry> jarEntries = jarFile.entries();
            
            while (jarEntries.hasMoreElements()) {
               JarEntry jarEntry = jarEntries.nextElement();

               if(!jarEntry.isDirectory()) {
                  String name = jarEntry.getName();
                  Matcher matcher = pattern.matcher(name);
                  
                  if(matcher.matches()) {
                     URL url = loader.getResource(name);
                     queue.offer(url);
                  }
               }
            }
            jarFile.close();
         } catch(Exception e) {
            throw new IllegalStateException("Could not list JAR file " + file, e);
         }
      }
   }
}