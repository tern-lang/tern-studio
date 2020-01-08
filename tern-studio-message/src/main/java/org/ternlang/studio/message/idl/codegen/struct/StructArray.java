package org.ternlang.studio.message.idl.codegen.struct;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.codegen.CodeTemplate;

public class StructArray extends CodeTemplate {

   public StructArray(Domain domain, Entity entity) {
      super(domain, entity);
   }
   
   @Override
   protected String name() {
      return entity.getName() + "Array";
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
      builder.append("Array extends Iterable<");
      builder.append(name);
      builder.append("> {");
      generateBody();
      builder.append("}\n");
   }
   
   @Override
   protected void generateBody() {
      String name = entity.getName();
      
      builder.append("\n");
      builder.append("   /**").append("\n");
      builder.append("    * Add element to the array\n");
      builder.append("    * @returns iterator of elements").append("\n");
      builder.append("    */").append("\n");  
      builder.append("   @Override\n");
      builder.append("   Iterator<").append(name).append("> iterator();\n");
      builder.append("\n");
      builder.append("   /**").append("\n");
      builder.append("    * Length of the array\n");
      builder.append("    * @returns number of elements").append("\n");
      builder.append("    */").append("\n");
      builder.append("   int length();\n");
      builder.append("\n");
   }
}