package org.ternlang.studio.message.idl.codegen.struct;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.codegen.CodeTemplate;

public class StructOption extends CodeTemplate {

   public StructOption(Domain domain, Entity entity) {
      super(domain, entity);
   }
   
   @Override
   protected String name() {
      return entity.getName() + "Option";
   }   

   @Override
   protected String category() {
      return "interface";
   }
   
   @Override
   protected void generateBody() {
      String name = entity.getName();
      
      appender.append("\n");
      appender.append("   /**").append("\n");
      appender.append("    * Deterning if it is present\n");
      appender.append("    * @returns true if present false otherwise").append("\n");
      appender.append("    */").append("\n");  
      appender.append("   boolean isPresent();\n");
      appender.append("\n");
      appender.append("   /**").append("\n");
      appender.append("    * Value if present\n");
      appender.append("    * @returns value if present").append("\n");
      appender.append("    */").append("\n");
      appender.append("   ").append(name).append(" get();\n");
      appender.append("\n");
   }
}