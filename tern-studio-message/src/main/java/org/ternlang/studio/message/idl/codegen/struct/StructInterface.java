package org.ternlang.studio.message.idl.codegen.struct;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.studio.message.idl.Property;
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
      generateProperties();
   }
   
   protected void generateProperties() {
      Package module = entity.getPackage();
      
      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      entity.getProperties().forEach(property -> {
         generateProperty(property);
      });
      
      builder.append("\n");
   }

   protected void generateProperty(Property property) {
      String constraint = property.getConstraint();
      String name = property.getName();
      String prefix = category().equals("interface") ? "" : "public ";
      
      builder.append("\n");
      
      if(prefix.isEmpty()) {
         builder.append("   /**").append("\n");
         builder.append("    * Gets ").append(name).append("\n");
         builder.append("    * @returns ").append(" current value").append("\n");
         builder.append("    */").append("\n");
         builder.append("   ");   
      } else {
         builder.append("   @Override\n");
         builder.append("   ");   
         
      }
      if(property.isArray()) {
         char first = constraint.charAt(0);
         String remainder = constraint.substring(1);
         
         builder.append(prefix);
         builder.append(Character.toUpperCase(first));
         builder.append(remainder);
         builder.append("Array");
      } else {
         builder.append(prefix);
         builder.append(constraint);    
      }
      builder.append(" ");
      builder.append(name);
      builder.append("()");
      generatePropertyBody(property);      
      builder.append("\n");
   }
   
   protected void generatePropertyBody(Property property) {
      builder.append(";");
   }

}
