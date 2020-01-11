package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class PrimitiveGenerator extends PropertyGenerator {

   public PrimitiveGenerator(Domain domain, Property property) {
      super(domain, property);
   }
   
   @Override
   public void generateField(CodeAppender builder) {
      // no field for primitive
   }
   
   @Override
   public void generateGetter(CodeAppender builder) {
      String constraint = property.getConstraint();
      String method = getConstraint(Case.PASCAL);
      String name = property.getName();
      int offset = property.getOffset();
      
      builder.append("   @Override\n");
      builder.append("   public %s %s() {\n", constraint, name);
      builder.append("      return buffer.get%s(offset + %s, ByteOrder.LITTLE_ENDIAN);\n", method, offset);
      builder.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender builder) {
      String constraint = property.getConstraint();
      String method = getConstraint(Case.PASCAL);
      String name = property.getName();
      int offset = property.getOffset();
      
      builder.append("   @Override\n");
      builder.append("   public void %s(%s %s) {\n", name, constraint, name);
      builder.append("      return buffer.set%s(offset + %s, (%s)%s, ByteOrder.LITTLE_ENDIAN);\n", 
            method, offset, constraint, name);
      builder.append("   }\n");
   }

   @Override
   public void generateGetterSignature(CodeAppender builder) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      builder.append("   %s %s();\n", constraint, name);
   }

   @Override
   public void generateSetterSignature(CodeAppender builder) {
      String constraint = property.getConstraint();
      String name = property.getName();
      
      builder.append("   void %s(%s %s);\n", name, constraint, name);
   }
}
