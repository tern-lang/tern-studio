package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Domain;

public class ImportList {

   private final Import[] imports;
   
   public ImportList(Import... imports) {
      this.imports = imports;
   }
   
   public void define(Scope scope, Domain domain) throws Exception {
      for(Import entry : imports) {
         entry.define(scope, domain);
      }
   }

   public void process(Scope scope, Domain domain) throws Exception {
      for(Import entry : imports) {
         entry.process(scope, domain);
      }
   }
}
