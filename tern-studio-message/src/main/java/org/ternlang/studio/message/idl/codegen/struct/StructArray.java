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
      
      appender.append("public ");
      appender.append(category);
      appender.append(" ");
      appender.append(name);
      appender.append("Array extends Iterable<");
      appender.append(name);
      appender.append("> {");
      generateBody();
      appender.append("}\n");
   }
   
   @Override
   protected void generateBody() {
      String name = entity.getName();
      
      appender.append("\n");
      appender.append("   /**").append("\n");
      appender.append("    * Add element to the array\n");
      appender.append("    * @returns iterator of elements").append("\n");
      appender.append("    */").append("\n");  
      appender.append("   @Override\n");
      appender.append("   Iterator<").append(name).append("> iterator();\n");
      appender.append("\n");
      appender.append("   /**").append("\n");
      appender.append("    * Length of the array\n");
      appender.append("    * @returns number of elements").append("\n");
      appender.append("    */").append("\n");
      appender.append("   int length();\n");
      appender.append("\n");
   }
}