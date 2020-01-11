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

         appender.append(separator);
         appender.append("   %s((byte)%s)", name, index++);
         separator = ",\n";         
      }
      appender.append(";\n\n");
      generateFields();
      generateConstructor();
      generateResolver();
   }
   
   private void generateFields() {
      appender.append("   public final byte code;\n\n");
   }
   
   private void generateConstructor() {
      String name = entity.getName();

      appender.append("   %s(byte code) {\n", name);
      appender.append("      this.code = code;\n");
      appender.append("   }\n");
   }

   private void generateResolver() {
      List<Property> properties = entity.getProperties();
      String name = entity.getName();
      int index = 1;

      appender.append("\n");
      appender.append("   public static %s resolve(int code) {\n", name);
      appender.append("      switch(code) {\n");

      for(Property property : properties) {
         String entry = property.getName();

         appender.append("      case %s: return %s;\n", index++, entry);
      }
      appender.append("      }\n");
      appender.append("      throw new IllegalArgumentException(\"No match for \" + code);\n");
      appender.append("   }\n");
   }
}
