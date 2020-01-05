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

   public void setType(PropertyType type) {
      this.type = type;
   }

   public String getConstraint() {
      return constraint;
   }

   public void setConstraint(String constraint) {
      this.constraint = constraint;
   }

   public String getName() {
      return name;
   }

   public int getDimension() {
      return dimension;
   }

   public void setDimension(int dimension) {
      this.dimension = dimension;
   }
}
