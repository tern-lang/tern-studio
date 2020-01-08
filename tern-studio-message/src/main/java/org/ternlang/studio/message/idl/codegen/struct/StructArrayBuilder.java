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
      
      builder.append("public ");
      builder.append(category);
      builder.append(" ");
      builder.append(name);
      builder.append("ArrayBuilder extends ");
      builder.append(name);
      builder.append("Array {");
      generateBody();
      builder.append("}\n");
   }
   
   @Override
   protected void generateBody() {
      String name = entity.getName();
      
      builder.append("\n");
      builder.append("   /**").append("\n");
      builder.append("    * Add element to the array\n");
      builder.append("    * @returns builder to use").append("\n");
      builder.append("    */").append("\n");   
      builder.append("   ").append(name).append("Builder add();");
      builder.append("\n");
   }
}

