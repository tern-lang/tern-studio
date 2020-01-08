package org.ternlang.studio.message.idl.codegen;

import java.util.List;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Property;

public class EnumClass extends CodeTemplate {

   public EnumClass(Domain domain, Entity entity) {
      super(domain, entity);
   }
   
   @Override
   protected String name() {
      return entity.getName();
   }
   
   @Override
   protected String category() {
      return "enum";
   }

   @Override
   protected void generateBody() {
      List<Property> properties = entity.getProperties();
      String separator = "";
      int index = 1;
      
      for(Property property : properties) {
         String name = property.getName();
      
         builder.append(separator);
         builder.append("   ");
         builder.append(name);
         builder.append("((byte)");
         builder.append(index++);
         builder.append(")");
         separator = ",\n";         
      }      
      builder.append(";\n\n");
      generateFields();
      generateConstructor();
   }
   
   private void generateFields() {
      builder.append("   public final byte code;\n\n");
   }
   
   private void generateConstructor() {
      String name = entity.getName();
      
      builder.append("   ");
      builder.append(name);
      builder.append("(byte code) {\n");
      builder.append("      this.code = code;\n");
      builder.append("   }\n");      
   }

}
