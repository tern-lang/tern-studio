package org.ternlang.studio.message.idl.codegen.struct;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.codegen.CodeTemplate;

public class StructBuilder extends CodeTemplate {

   public StructBuilder(Domain domain, Entity entity) {
      super(domain, entity);
   }

   @Override
   protected String name() {
      return entity.getName() + "Builder";
   }

   @Override
   protected String category() {
      return "interface";
   }

   @Override
   protected void generateEntity() {
      String name = entity.getName();
      String category = category();

      appender.append("public ");
      appender.append(category);
      appender.append(" ");
      appender.append(name);
      appender.append("Builder implements ");
      appender.append(name);
      appender.append(" {");
      generateBody();
      appender.append("}\n");
   }

   @Override
   protected void generateBody() {
      appender.append("\n");
      builder.create(entity).stream().forEach(generator -> {
         generator.generateSetterSignature(appender);
      });
   }
}
