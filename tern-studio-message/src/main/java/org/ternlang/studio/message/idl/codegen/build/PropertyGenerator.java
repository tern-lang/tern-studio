package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public abstract class PropertyGenerator {

   public final Property property;
   public final Domain domain;

   protected PropertyGenerator(Domain domain, Property property) {
      this.domain = domain;
      this.property = property;
   }

   public Domain getDomain() {
      return domain;
   }

   public Property getProperty() {
      return property;
   }

   public String getConstraint(Case textCase) {
      String constraint = property.getConstraint();
      char first = constraint.charAt(0);
      String remainder = constraint.substring(1);

      if(textCase == Case.CAMEL) {
         return String.format("%s%s", Character.toLowerCase(first), remainder);
      }
      if(textCase == Case.PASCAL) {
         return String.format("%s%s", Character.toUpperCase(first), remainder);
      }
      return constraint;
   }
   
   public abstract void generateField(CodeAppender builder);
   public abstract void generateGetter(CodeAppender builder);
   public abstract void generateSetter(CodeAppender builder);
   public abstract void generateGetterSignature(CodeAppender builder);
   public abstract void generateSetterSignature(CodeAppender builder);

   public enum Case {
      CAMEL,
      PASCAL,
      NORMAL
   }
}
