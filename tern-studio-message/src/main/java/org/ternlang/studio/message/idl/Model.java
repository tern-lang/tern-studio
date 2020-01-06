package org.ternlang.studio.message.idl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;

public class Model {

   private final Cache<String, Package> packages;

   public Model() {
      this.packages = new CopyOnWriteCache<String, Package>();
   }
   
   public Package addPackage(String name) {
      Package module = packages.fetch(name);
      
      if(module == null) {
         module = new Package(name);
         packages.cache(name, module);
      }
      return module;
   }
   
   public Package getPackage(String name) {
      return packages.fetch(name);
   }

   public List<Package> getPackages() {
      return packages.keySet()
            .stream()
            .map(packages::fetch)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
   } 
}
