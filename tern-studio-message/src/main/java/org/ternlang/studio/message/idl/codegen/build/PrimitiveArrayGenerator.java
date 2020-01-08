package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class PrimitiveArrayGenerator implements PropertyGenerator {

   @Override
   public void generateField(CodeAppender builder, String owner, Property property) {
      String name = property.getName();
      String constraint = property.getConstraint();
      String type = generateName(constraint);

      builder.append("   private %sArrayCodec %s = new %sArrayCodec();\n", type, name, type);
   }
   
   @Override
   public void generateGetter(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String type = generateName(constraint);
      String upperType = type.toUpperCase();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      builder.append("   @Override\n");
      builder.append("   public %sArrayBuilder %s() {\n", type, name);
      builder.append("      %s.wrap(buffer, offset + %s, %s * Primitive.%s_SIZE);\n", name, offset, length, upperType);
      builder.append("      return %s;\n", name);
      builder.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String type = generateName(constraint);
      String upperType = type.toUpperCase();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      if(!constraint.equals("char")) {
         builder.append("   @Override\n");
         builder.append("   public void %s(CharSequence %s) {\n", name, name);
         builder.append("      int length = %s.length();\n", name);
         builder.append("\n");
         builder.append("      if(length > %s) {\n", length);
         builder.append("         throw new IllegalArgumentException(\"Length exceeds %s\");\n", length);
         builder.append("      }\n");
         builder.append("      for(int i = 0; i < length; i++) {\n");
         builder.append("         char next = %s.charAt(i);\n");
         builder.append("         buffer.put%s((offset + %s) + (i * Primitive.%s_SIZE), next, ByteOrder.LITTLE_ENDIAN);\n", type, upperType, offset);
         builder.append("      }\n");
         builder.append("   }\n");
         builder.append("\n");
      }
   }

   @Override
   public void generateGetterSignature(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String type = generateName(constraint);
      String name = property.getName();
      
      builder.append("   %sArray %s();\n", type, name);
   }

   @Override
   public void generateSetterSignature(CodeAppender builder, String owner, Property property) {
      String constraint = property.getConstraint();
      String type = generateName(constraint);
      String name = property.getName();
      
      if(!constraint.equals("char")) {
         builder.append("   void %s(CharSequence %s);\n", name, name);
      }
      builder.append("   %sArrayBuilder %s();\n", type, name);
   }
}
