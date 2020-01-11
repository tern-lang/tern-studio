package org.ternlang.studio.message.idl.codegen.build;

import java.util.List;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class UnionGenerator extends PropertyGenerator {

   public UnionGenerator(Domain domain, Entity entity, Property property) {
      super(domain, entity, property);
   }

   @Override
   public void generateField(CodeAppender appender) {
      String name = property.getName();
      String constraint = property.getConstraint();

      appender.append("   private %sCodec %sCodec = new %sCodec();\n", constraint, name, constraint);
   }
   
   @Override
   public void generateGetter(CodeAppender appender) {
      String constraint = property.getConstraint();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      appender.append("   @Override\n");
      appender.append("   public %sAppender %s() {\n", constraint, name);
      appender.append("      %sCodec.with(frame, offset + %s, %s * ByteSize.BYTE_SIZE);\n", name, offset, length);
      appender.append("      return %s;\n", name);
      appender.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender appender) {
      String constraint = property.getConstraint();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      appender.append("   @Override\n");
      appender.append("   public void %s(%s, %s) {\n", name, constraint, name);
      appender.append("      %sCodec.with(frame, offset + %s, ??);\n", name, offset, length);
      
      Entity entity = domain.getEntity(constraint);
      List<Property> properties = entity.getProperties();
      
      for(Property entry : properties) {
         String identifier = entry.getName();
         appender.append("      %sCodec.%s(%s.%s());\n", name, identifier, name, identifier);
      }
      appender.append("      return %s;\n", name);
      appender.append("   }\n");
   }

   @Override
   public void generateGetterSignature(CodeAppender appender) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      appender.append("   %s %s();\n", constraint, name);
   }

   @Override
   public void generateSetterSignature(CodeAppender appender) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      appender.append("   %sAppender %s();\n", constraint, name);
      appender.append("   void %s(%s, %s);\n", name, constraint, name);
   }
}