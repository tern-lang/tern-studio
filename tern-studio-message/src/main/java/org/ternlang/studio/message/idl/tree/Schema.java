package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Package;

public class Schema {

   private final Definition[] definitions;
   private final Namespace namespace;
   private final ImportList imports;
   
   public Schema(Namespace namespace, ImportList imports, Definition... definitions) {
      this.definitions = definitions;
      this.namespace = namespace;
      this.imports = imports;
   }
   
   public void define(Scope scope, Domain domain) throws Exception {
      Package module = namespace.define(scope, domain);
      
      imports.define(scope, domain);
      
      for(Definition definition : definitions) {
         definition.define(scope, module);
      }
   }
   
   public void process(Scope scope, Domain domain) throws Exception {
      Package module = namespace.process(scope, domain);
      
      module.setScope(scope);
      imports.process(scope, domain);
      
      for(Definition definition : definitions) {
         definition.process(scope, module);
      }
   }
}
