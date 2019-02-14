package org.ternlang.studio.common;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class FileProcessor<T> {

   private final Map<String, Map<File, FileExecutor>> executors;
   private final Map<String, Set<File>> active;
   private final FilePatternWalker walker;
   private final FileAction<T> action;
   private final Executor executor;
   
   public FileProcessor(FileAction<T> action, Executor executor) {
      this.executors = new ConcurrentHashMap<String, Map<File, FileExecutor>>();
      this.active = new ConcurrentHashMap<String, Set<File>>();
      this.walker = new FilePatternWalker();
      this.action = action;
      this.executor = executor;
   }
   
   public Set<T> process(String reference, String pattern) throws Exception {
      Map<File, FileExecutor> referenceBatch = executors.get(reference);
      Set<File> referenceFiles = active.get(reference);
      List<File> matchesFiles = walker.walk(pattern);
      
      if(referenceBatch == null) {
         referenceBatch = new ConcurrentHashMap<File, FileExecutor>();
         referenceFiles = new CopyOnWriteArraySet<File>();
         executors.put(reference, referenceBatch); // file executors
         active.put(reference, referenceFiles); // active files
      } 
      for(File matchedFile : matchesFiles) {
         File canonicalFile = matchedFile.getCanonicalFile();
         FileExecutor executor = referenceBatch.get(canonicalFile);
         
         if(executor == null) {
            executor = new FileExecutor(reference, canonicalFile);
            referenceBatch.put(canonicalFile, executor);
         }
         referenceFiles.add(canonicalFile);
      }
      int count = referenceFiles.size(); 
      
      if(count > 0) {
         Set<T> results = new CopyOnWriteArraySet<T>();
         CountDownLatch latch = new CountDownLatch(count);
      
         for(File referenceFile : referenceFiles) {
            FileTask task = new FileTask(latch, reference, referenceFile, results);
            executor.execute(task);
         }
         latch.await(5000, TimeUnit.MILLISECONDS); // don't wait forever
         return results;
      }
      return Collections.emptySet();
   }
   
   private class FileTask implements Runnable {
      
      private final CountDownLatch latch;
      private final String reference;
      private final File file;
      private final Set<T> results;
      
      public FileTask(CountDownLatch latch, String reference, File file, Set<T> results) {
         this.reference = reference;
         this.results = results;
         this.latch = latch;
         this.file = file;
      }
      
      @Override
      public void run() {
         Map<File, FileExecutor> batch = executors.get(reference);
         FileExecutor executor = batch.get(file);
         
         if(executor != null) {
            try {
               executor.execute(results);
            } finally {
               latch.countDown();
            }
         }
      }
   }
   
   private class FileExecutor  {
      
      private final AtomicReference<T> result;
      private final AtomicLong update;
      private final String reference;
      private final File file;
      
      public FileExecutor(String reference, File file) {
         this.result = new AtomicReference<T>();
         this.update = new AtomicLong();
         this.reference = reference;
         this.file = file;
      }
      
      public void execute(Set<T> results) {
         Map<File, FileExecutor> referenceBatch = executors.get(reference);
         Set<File> referenceFiles = active.get(reference);
         long time = System.currentTimeMillis();
         long modified = file.lastModified();
         long last = update.get();
         
         if(file.exists()) {
            if(last <= modified) {
               try {
                  T value = action.execute(reference, file);
                  
                  if(value != null) {
                     results.add(value);
                  }
                  result.set(value); // cache result
                  update.set(time); // record update
               }catch(Exception e) {
                  // e.printStackTrace();
                  referenceBatch.remove(file); // remove file as it caused problem
                  referenceFiles.remove(file);
               }
            } else {
               T value = result.get(); // use existing result as file has not changed
               
               if(value != null) {
                  results.add(value);
               }
            }
         } else {
            referenceBatch.remove(file); // file no longer exists
            referenceFiles.remove(file);
         }
         
      }
   }
}