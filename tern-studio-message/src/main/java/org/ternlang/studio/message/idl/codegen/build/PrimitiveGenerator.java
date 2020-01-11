package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public class PrimitiveGenerator extends PropertyGenerator {

   public PrimitiveGenerator(Domain domain, Entity entity, Property property) {
      super(domain, entity, property);
   }
   
   @Override
   public void generateField(CodeAppender appender) {
      // no field for primitive
   }
   
   @Override
   public void generateGetter(CodeAppender appender) {
      String constraint = property.getConstraint();
      String method = getConstraint(Case.PASCAL);
      String name = property.getName();
      int offset = property.getOffset();
      
      appender.append("   @Override\n");
      appender.append("   public %s %s() {\n", constraint, name);
      appender.append("      return frame.get%s(offset + %s);\n", method, offset);
      appender.append("   }\n");
   }

   @Override
   public void generateSetter(CodeAppender appender) {
      String constraint = property.getConstraint();
      String method = getConstraint(Case.PASCAL);
      String name = property.getName();
      int offset = property.getOffset();
      
      appender.append("   @Override\n");
      appender.append("   public void %s(%s %s) {\n", name, constraint, name);
      appender.append("      frame.set%s(offset + %s, (%s)%s);\n",
            method, offset, constraint, name);
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
      
      appender.append("   void %s(%s %s);\n", name, constraint, name);
   }
}
