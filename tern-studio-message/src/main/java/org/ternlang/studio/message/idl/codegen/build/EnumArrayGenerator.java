package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class EnumArrayGenerator extends PropertyGenerator {

   public EnumArrayGenerator(Domain domain, Property property) {
      super(domain, property);
   }
   
   @Override
   public void generateField(CodeAppender builder) {
      String name = property.getName();
      String constraint = property.getConstraint();

      builder.append("   private %sArrayCodec %sCodec = new %sArrayCodec();\n", constraint, name, constraint);
   }
   
   @Override
   public void generateGetter(CodeAppender builder) {
      String constraint = property.getConstraint();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      builder.append("   @Override\n");
      builder.append("   public %sArrayBuilder %s() {\n", constraint, name);
      builder.append("      %sCodec.wrap(buffer, offset + %s, %s * Primitive.BYTE_SIZE);\n", name, offset, length);
      builder.append("      return %sCodec;\n", name);
      builder.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender builder) {
      // setter is a getter
   }

   @Override
   public void generateGetterSignature(CodeAppender builder) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      builder.append("   %sArray %s();\n", constraint, name);
   }

   @Override
   public void generateSetterSignature(CodeAppender builder) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      builder.append("   %sArrayBuilder %s();\n", constraint, name);
   }
}
