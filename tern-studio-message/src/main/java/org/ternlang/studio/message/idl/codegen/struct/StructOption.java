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
      
      builder.append("\n");
      builder.append("   /**").append("\n");
      builder.append("    * Deterning if it is present\n");
      builder.append("    * @returns true if present false otherwise").append("\n");
      builder.append("    */").append("\n");  
      builder.append("   boolean isPresent();\n");
      builder.append("\n");
      builder.append("   /**").append("\n");
      builder.append("    * Value if present\n");
      builder.append("    * @returns value if present").append("\n");
      builder.append("    */").append("\n");
      builder.append("   ").append(name).append(" get();\n");
      builder.append("\n");
   }
}