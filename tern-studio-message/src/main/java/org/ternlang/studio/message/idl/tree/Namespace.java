package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.tree.Qualifier;

public class Namespace {

   private final Qualifier qualifier;
   private final Path path;
   
   public Namespace(Qualifier qualifier, Path path) {
      this.qualifier = qualifier;
      this.path = path;
   }
   
   public Package define(Scope scope, Domain domain) throws Exception {
      String location = qualifier.getQualifier();
      Package module = domain.addPackage(location);
      String resource = path.getPath();
      
      if(resource.startsWith("/")) {
         resource = resource.substring(1);
      }
      module.setPath(resource);
      return module;
   }
   
   public Package process(Scope scope, Domain domain) throws Exception {
      String location = qualifier.getQualifier();
      return domain.getPackage(location);
   }
}
