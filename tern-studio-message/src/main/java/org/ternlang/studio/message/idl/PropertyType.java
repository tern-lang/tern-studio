package org.ternlang.studio.message.idl;

public enum PropertyType {
   ARRAY(0b00001),
   PRIMITIVE(0b00010),
   ENTITY(0b00100),
   ENUM(0b01000);
   
   public final int mask;
   
   private PropertyType(int mask) {
      this.mask = mask;
   }
   
   public static boolean isEntity(int mask) {
      return (ENTITY.mask & mask) == ENTITY.mask;
   }
   
   public static boolean isPrimitive(int mask) {
      return (PRIMITIVE.mask & mask) == PRIMITIVE.mask;
   }
   
   public static boolean isArray(int mask) {
      return (ARRAY.mask & mask) == ARRAY.mask;
   }
   
   public static boolean isEnum(int mask) {
      return (ENUM.mask & mask) == ENUM.mask;
   }
}
