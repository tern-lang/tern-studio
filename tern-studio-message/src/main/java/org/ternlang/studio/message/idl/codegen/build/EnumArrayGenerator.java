package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class EnumArrayGenerator extends PropertyGenerator {

   public EnumArrayGenerator(Domain domain, Entity entity, Property property) {
      super(domain, entity, property);
   }
   
   @Override
   public void generateField(CodeAppender appender) {
      String name = property.getName();
      String constraint = property.getConstraint();

      appender.append("   private %sArrayCodec %sCodec = new %sArrayCodec();\n", constraint, name, constraint);
   }
   
   @Override
   public void generateGetter(CodeAppender appender) {
      String constraint = property.getConstraint();
      String name = property.getName();
      int length = property.getDimension();
      int offset = property.getOffset();
      
      appender.append("   @Override\n");
      appender.append("   public %sArrayBuilder %s() {\n", constraint, name);
      appender.append("      %sCodec.wrap(frame, offset + %s, %s * ByteSize.BYTE_SIZE);\n", name, offset, length);
      appender.append("      return %sCodec;\n", name);
      appender.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender appender) {
      // setter is a getter
   }

   @Override
   public void generateGetterSignature(CodeAppender appender) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      appender.append("   %sArray %s();\n", constraint, name);
   }

   @Override
   public void generateSetterSignature(CodeAppender appender) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      appender.append("   %sArrayBuilder %s();\n", constraint, name);
   }
}
