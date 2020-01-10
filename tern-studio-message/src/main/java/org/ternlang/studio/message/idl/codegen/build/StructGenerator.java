package org.ternlang.studio.message.idl.codegen.build;

import java.util.List;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class StructGenerator implements PropertyGenerator {
   
   private final Domain domain;
   
   public StructGenerator(Domain domain) {
      this.domain = domain;
   }

   @Override
   public void generateField(CodeAppender builder, String owner, Property property) {
      String name = property.getName();
      String constraint = property.getConstraint();

      builder.append("   private %sCodec %sCodec = new %sCodec();\n", constraint, name, constraint);
   }
   
   @Override
   public void generateGetter(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      builder.append("   @Override\n");
      builder.append("   public %sBuilder %s() {\n", constraint, name);
      builder.append("      %sCodec.wrap(buffer, offset + %s, %s * Primitive.BYTE_SIZE);\n", name, offset, length);
      builder.append("      return %s;\n", name);
      builder.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      builder.append("   @Override\n");
      builder.append("   public void %s(%s, %s) {\n", name, constraint, name);
      builder.append("      %sCodec.wrap(buffer, offset + %s, ??);\n", name, offset, length);
      
      Entity entity = domain.getEntity(constraint);
      List<Property> properties = entity.getProperties();
      
      for(Property entry : properties) {
         String identifier = entry.getName();
         builder.append("      %sCodec.%s(%s.%s());\n", name, identifier, name, identifier);
      }
      builder.append("      return %s;\n", name);
      builder.append("   }\n");
   }

   @Override
   public void generateGetterSignature(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      builder.append("   %s %s();\n", constraint, name);
   }

   @Override
   public void generateSetterSignature(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      builder.append("   %sBuilder %s();\n", constraint, name);
      builder.append("   void %s(%s, %s);\n", name, constraint, name);
   }
}
