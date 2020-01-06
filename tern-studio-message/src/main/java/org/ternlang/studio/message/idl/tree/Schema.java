package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Model;
import org.ternlang.studio.message.idl.Package;

public class Schema {

   private final Definition[] definitions;
   private final Namespace namespace;
   
   public Schema(Namespace namespace, Definition... definitions) {
      this.definitions = definitions;
      this.namespace = namespace;
   }
   
   public void define(Scope scope, Model model) throws Exception {
      Package module = namespace.define(scope, model);
      
      for(Definition definition : definitions) {
         definition.define(scope, module);
      }
   }
   
   public void process(Scope scope, Model model) throws Exception {
      Package module = namespace.process(scope, model);
      
      for(Definition definition : definitions) {
         definition.process(scope, module);
      }
   }
}
