package tern.studio.agent.profiler;

import static tern.core.Reserved.SCRIPT_EXTENSION;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import tern.common.Cache;
import tern.common.CopyOnWriteCache;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.trace.Trace;
import tern.studio.agent.debug.TraceAdapter;

public class TraceProfiler extends TraceAdapter {
   
   private final Cache<String, ResourceProfiler> profilers;
   private final Set<String> resources;
   
   public TraceProfiler() {
      this.profilers = new CopyOnWriteCache<String, ResourceProfiler>();
      this.resources = new CopyOnWriteArraySet<String>();
   }

   public SortedSet<ProfileResult> lines(int size) {
      SortedSet<ProfileResult> results = new TreeSet<ProfileResult>();
      SortedSet<ProfileResult> reduced = new TreeSet<ProfileResult>();
      
      for(String resource : resources){
         ResourceProfiler profiler = profilers.fetch(resource);
         
         if(profiler != null) {
            profiler.collect(results, size);
         }
      }
      Iterator<ProfileResult> iterator = results.iterator();
      
      while(iterator.hasNext()) {
         if(size-- <= 0) {
            break;
         }
         ProfileResult result = iterator.next();
         
         if(result != null) {
            long duration = result.getTime();
            
            if(duration > 0) {
               reduced.add(result);
            }
         }
      }
      return reduced;
   }
   
   @Override
   public void traceBefore(Scope scope, Trace trace) {
      Path path = trace.getPath();
      String resource = path.getPath();
      ResourceProfiler profiler = profilers.fetch(resource);
      int line = trace.getLine();
      
      if(profiler == null) {
         String local = resource;
         
         if(!local.endsWith(SCRIPT_EXTENSION)) { // a.b.c
            local = local.replace('.', '/'); // a/b/c
            local = local + SCRIPT_EXTENSION; // a/b/c.snap
         }
         if(!local.startsWith("/")) {
            local = "/" + local; // /a/b/c.snap
         }
         profiler = profilers.fetch(local);        
         
         if(resources.add(local)) {
            if(profiler == null) {
               profiler = new ResourceProfiler(local);
            }
            profilers.cache(local, profiler);
            profilers.cache(resource, profiler);
            resources.add(resource);
         }
      }
      if(profiler != null) { // eval(...) could be null
         profiler.enter(line);
      }
   }

   @Override
   public void traceAfter(Scope scope, Trace trace) {
      Path path = trace.getPath();
      String resource = path.getPath();
      ResourceProfiler profiler = profilers.fetch(resource);
      int line = trace.getLine();
      
      if(profiler != null) {
         profiler.exit(line);
      }
   }
}