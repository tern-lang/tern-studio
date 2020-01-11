package org.ternlang.studio.message.idl.codegen.struct;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.codegen.CodeTemplate;

public class StructArrayBuilder extends CodeTemplate {

   public StructArrayBuilder(Domain domain, Entity entity) {
      super(domain, entity);
   }

   @Override
   protected String name() {
      return entity.getName() + "ArrayBuilder";
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
      appender.append("ArrayBuilder extends ");
      appender.append(name);
      appender.append("Array {");
      generateBody();
      appender.append("}\n");
   }

   @Override
   protected void generateBody() {
      String name = entity.getName();

      appender.append("\n");
      appender.append("   /**").append("\n");
      appender.append("    * Add element to the array\n");
      appender.append("    * @returns builder to use").append("\n");
      appender.append("    */").append("\n");
      appender.append("   ").append(name).append("Builder add();");
      appender.append("\n");
   }
}

