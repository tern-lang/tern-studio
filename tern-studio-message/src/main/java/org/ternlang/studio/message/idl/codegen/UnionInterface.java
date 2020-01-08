package org.ternlang.studio.message.idl.codegen;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;

public class UnionInterface extends CodeTemplate {

   public UnionInterface(Domain domain, Entity entity) {
      super(domain, entity);
   }
   
   @Override
   protected String name() {
      return entity.getName();
   }
   
   @Override
   protected String category() {
      return "interface";
   }
   
   @Override
   protected void generateBody() {
   }
}
