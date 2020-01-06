package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Model;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.tree.Qualifier;

public class Namespace {

   private final Qualifier qualifier;
   private final Path path;
   
   public Namespace(Qualifier qualifier, Path path) {
      this.qualifier = qualifier;
      this.path = path;
   }
   
   public Package define(Scope scope, Model model) throws Exception {
      String module = qualifier.getQualifier();
      return model.addPackage(module);
   }
   
   public Package process(Scope scope, Model model) throws Exception {
      String module = qualifier.getQualifier();
      return model.getPackage(module);
   }
}
