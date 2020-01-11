package org.ternlang.studio.message.idl.codegen.struct;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.codegen.CodeTemplate;

public class StructInterface extends CodeTemplate {

   public StructInterface(Domain domain, Entity entity) {
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
      builder.create(entity).stream().forEach(generator -> {
         generator.generateGetterSignature(appender);
      });
   }
}
