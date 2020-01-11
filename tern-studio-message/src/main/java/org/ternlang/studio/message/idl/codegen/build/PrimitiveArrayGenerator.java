package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class PrimitiveArrayGenerator extends PropertyGenerator {

   public PrimitiveArrayGenerator(Domain domain, Entity entity, Property property) {
      super(domain, entity, property);
   }
   
   @Override
   public void generateField(CodeAppender appender) {
      String name = property.getName();
      String type = getConstraint(Case.PASCAL);

      appender.append("   private %sArrayCodec %sCodec = new %sArrayCodec();\n", type, name, type);
   }
   
   @Override
   public void generateGetter(CodeAppender appender) {
      String type = getConstraint(Case.PASCAL);
      String upperType = type.toUpperCase();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      appender.append("   @Override\n");
      appender.append("   public %sArrayBuilder %s() {\n", type, name);
      appender.append("      %sCodec.with(frame, offset + %s, %s * ByteSize.%s_SIZE);\n", name, offset, length, upperType);
      appender.append("      return %sCodec;\n", name);
      appender.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender appender) {
      String constraint = property.getConstraint();
      String type = getConstraint(Case.PASCAL);
      String upperType = type.toUpperCase();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      if(constraint.equals("char")) {
         appender.append("   @Override\n");
         appender.append("   public void %s(CharSequence %s) {\n", name, name);
         appender.append("      int length = %s.length();\n", name);
         appender.append("\n");
         appender.append("      if(length > %s) {\n", length);
         appender.append("         throw new IllegalArgumentException(\"Length exceeds %s\");\n", length);
         appender.append("      }\n");
         appender.append("      for(int i = 0; i < length; i++) {\n");
         appender.append("         char next = %s.charAt(i);\n", name);
         appender.append("         frame.set%s((offset + %s) + (i * ByteSize.%s_SIZE), next);\n", type, offset, upperType);
         appender.append("      }\n");
         appender.append("   }\n");
         appender.append("\n");
      }
   }

   @Override
   public void generateGetterSignature(CodeAppender appender) {
      String type = getConstraint(Case.PASCAL);
      String name = property.getName();
      
      appender.append("   %sArray %s();\n", type, name);
   }

   @Override
   public void generateSetterSignature(CodeAppender appender) {
      String constraint = property.getConstraint();
      String type = getConstraint(Case.PASCAL);
      String name = property.getName();
      
      if(constraint.equals("char")) {
         appender.append("   void %s(CharSequence %s);\n", name, name);
      }
      appender.append("   %sArrayBuilder %s();\n", type, name);
   }
}
