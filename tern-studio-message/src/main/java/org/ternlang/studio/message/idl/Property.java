package org.ternlang.studio.message.idl;

public class Property {
   
   private final PropertyType type;
   private final String constraint;
   private final String name;
   private final int dimension;
   
   public Property(PropertyType type, String name, String constraint) {
      this(type, name, constraint, 0);
   }
   
   public Property(PropertyType type, String name, String constraint, int dimension) {
      this.dimension = dimension;
      this.constraint = constraint;
      this.type = type;
      this.name = name;
   }
   
   public PropertyType getType() {
      return type;
   }
  
   public String getName() {
      return name;
   }
   
   public String getConstraint() {
      return constraint;
   }
   
   public int getDimension() {
      return dimension;
   }
}
