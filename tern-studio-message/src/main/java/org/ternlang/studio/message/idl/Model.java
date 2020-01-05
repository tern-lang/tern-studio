package org.ternlang.studio.message.idl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.common.Cache;
import org.ternlang.common.LazyCache;

public class Model {

   private Cache<String, Package> packages;

   public Model() {
      this.packages = new LazyCache<String, Package>(Package::new);
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
