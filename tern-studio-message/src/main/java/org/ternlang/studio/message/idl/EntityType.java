package org.ternlang.studio.message.idl;

public enum EntityType {
   ENUM,
   STRUCT,
   UNION,
   PRIMITIVE;
   
   public boolean isEnum() {
      return this == ENUM;
   }
   
   public boolean isStruct() {
      return this == STRUCT;
   }
   
   public boolean isUnion() {
      return this == UNION;
   }
   
   public boolean isPrimitive() {
      return this == PRIMITIVE;
   }
}
