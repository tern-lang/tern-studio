package org.ternlang.studio.message.idl;

public class Property {
   
   private PropertyType type;
   private String constraint;
   private String name;
   private int dimension;
   
   public Property(String name) {
      this.name = name;
   }

   public PropertyType getType() {
      return type;
   }

   public Property setType(PropertyType type) {
      this.type = type;
      return this;
   }

   public String getConstraint() {
      return constraint;
   }

   public Property setConstraint(String constraint) {
      this.constraint = constraint;
      return this;
   }

   public String getName() {
      return name;
   }

   public int getDimension() {
      return dimension;
   }

   public Property setDimension(int dimension) {
      this.dimension = dimension;
      return this;
   }
}
