package org.ternlang.studio.resource.boot;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ternlang.core.Any;
import org.ternlang.core.Bug;
import org.ternlang.core.ContextClassLoader;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

public class PathMatchingClassScanner {
   
   @Bug("should use this")
   private final ClassLoader loader;
   
   public PathMatchingClassScanner() {
      this.loader = new ContextClassLoader(Any.class);
   }
   
   public Iterator<ClassInfo> findClasses(String prefix) {
      return new ClassGraph()
            .enableAllInfo()
            //.disableDirScanning()
            .whitelistPackages(prefix + ".*")
            .whitelistPaths("..")
            .scan()
            .getAllClasses()
            .iterator();
   }
   
   public Set<Class> findClasses(String prefix, Class<? extends Annotation> annotation) {
      Set<Class> types = new HashSet<Class>();
      Iterator<ClassInfo> iterator = new ClassGraph()
            .enableAllInfo()
            //.disableDirScanning()
            .whitelistPackages(prefix + ".*")
            .whitelistPaths("..")
            //.verbose()
            .scan()
            .getAllClasses()
            .iterator();
      
     String name = annotation.getName();  
     while(iterator.hasNext()) {
        ClassInfo info = iterator.next();
        
        if(info.hasAnnotation(name)) {
           Class type = info.loadClass();
           types.add(type);
        }
     }
     return types;
   }
}
