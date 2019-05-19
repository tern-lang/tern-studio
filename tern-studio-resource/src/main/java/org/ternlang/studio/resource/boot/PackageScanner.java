package org.ternlang.studio.resource.boot;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.ternlang.studio.resource.action.annotation.Component;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.MethodParameterInfo;

public class PackageScanner implements DependencyScanner {
   
   private final ClassGraph graph;
   private final String prefix;

   public PackageScanner(String prefix) {
      this.graph = new ClassGraph()
         .enableAllInfo()
         //.disableDirScanning()
         .whitelistPackages(prefix + ".*")
         .whitelistPaths("..");
      this.prefix = prefix;
   }
   
   @Override
   public Queue<Class> scan() {
      World world = classesWithAnnotation(Component.class);
      LinkedList<Class> ready = new LinkedList<>();
      LinkedList<ClassInfo> resolving = new LinkedList<>();
      Set<ClassInfo> done = new HashSet<>();
      
      resolving.addAll(world.components.values());
      
      while(!resolving.isEmpty()) {
         ClassInfo next = resolving.poll();
         Set<ClassInfo> children = getChildren(next, world);
         
         children.removeAll(done);
         
         if(!children.isEmpty()) {
            for(ClassInfo child : children) {
               resolving.offer(child);
            }
            resolving.offer(next);
         } else {
            if(done.add(next)) {
               Class type = next.loadClass();
               ready.offer(type);
            }
         }
      }
      return ready;
   }
   
   private World classesWithAnnotation(Class<? extends Annotation> annotation) {
      World world = new World(prefix);
      Iterator<ClassInfo> iterator = graph
            .scan()
            .getAllClasses()
            .iterator();
      
     String name = annotation.getName();  
     while(iterator.hasNext()) {
        ClassInfo info = iterator.next();
        
        if(info.hasAnnotation(name)) {
           world.components.put(info.getName(), info);
        }
        world.other.put(info.getName(), info);
     }
     return world;
   }
   
   private Set<ClassInfo> getChildren(ClassInfo info, World world) {
      Set<ClassInfo> done = new HashSet<>();
      MethodInfoList constructors = info.getConstructorInfo();
      int size = constructors.size();
      
      for(int i = 0; i < size; i++) {
         MethodInfo constructor = constructors.get(i);
         MethodParameterInfo[] params = constructor.getParameterInfo();
         
         for(MethodParameterInfo param : params) {
            String name = param.getTypeDescriptor().toString();
            
            if(name.startsWith(world.prefix)) {
               ClassInfo paramInfo = world.other.get(name);
               
               if(paramInfo != null && !paramInfo.isEnum() && !name.contains("ActionMatcher")) {
                  paramInfo = componentType(paramInfo, world);
                  
                  if(paramInfo == null) {
                     throw new RuntimeException("Could not resolve type for " + name);
                  }
                  done.add(paramInfo);
               } 
            }
         }
      }
      return done;
   }
   
   private ClassInfo componentType(ClassInfo info, World world) {
      String rootName = info.getName();
      
      if(!world.components.containsKey(rootName)) {
         if(info.isInterface()) {
            ClassInfoList list = info.getClassesImplementing();
            for(int i = 0; i < list.size(); i++) {
               ClassInfo next = list.get(i);
               String name = next.getName();
               
               if(world.components.containsKey(name)) {
                  return world.components.get(name);
               }
            }
            return null;
         }
         for(ClassInfo next : world.components.values()) {
            if(next.extendsSuperclass(rootName)) {
               return next;
            }
         }
         return null;
      }
      return world.components.get(rootName);
   }
   
   
   private static class World {
      public final Map<String, ClassInfo> components = new HashMap<>();
      public final Map<String, ClassInfo> other = new HashMap<>();
      public final String prefix;
      public World(String prefix) {
         this.prefix = prefix;
      }
   }
   

}
