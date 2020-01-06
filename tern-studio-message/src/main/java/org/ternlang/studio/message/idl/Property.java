package org.ternlang.studio.message.idl;

public class Property {
   
   private String constraint;
   private String name;
   private int dimension;
   private boolean optional;
   private int type;
   
   public Property(String name) {
      this.name = name;
   }
   
   public boolean isOptional() {
      return optional;
   }

   public void setOptional(boolean optional) {
      this.optional = optional;
   }

   public int getType() {
      return type;
   }

   public Property setType(int type) {
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
