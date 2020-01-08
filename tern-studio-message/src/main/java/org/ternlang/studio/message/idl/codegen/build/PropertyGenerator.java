package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public interface PropertyGenerator {
   
   default String generateName(String token) {
      char first = token.charAt(0);
      String remainder = token.substring(1);
      
      return String.format("%s%s", first, remainder);
   }
   
   void generateField(CodeAppender builder, String owner, Property property);
   void generateGetter(CodeAppender builder, String owner, Property property);
   void generateSetter(CodeAppender builder, String owner, Property property);
   void generateGetterSignature(CodeAppender builder, String owner, Property property);
   void generateSetterSignature(CodeAppender builder, String owner, Property property);
}
