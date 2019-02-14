package org.ternlang.studio.index;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public enum IndexType {
   SCRIPT("script"),
   IMPORT("import", "script", "module"),
   SUPER("super", "class", "trait", "enum"),
   FUNCTION("function", "script"), 
   CONSTRUCTOR("constructor", "class", "enum"), 
   MEMBER_FUNCTION("member-function", "module", "class", "trait", "enum"), 
   VARIABLE("variable", "script", "function", "member-function", "constructor", "compound"), 
   PROPERTY("property", "module", "class", "enum", "trait"), 
   CLASS("class", "script", "module", "class", "trait", "enum"),
   ENUM("enum", "script", "module", "class", "trait", "enum"),
   TRAIT("trait", "script", "module", "class", "trait", "enum"),
   MODULE("module", "script"),
   PARAMETER("parameter", "script", "function", "member-function", "constructor"),
   COMPOUND("compound", "script", "function", "member-function", "constructor", "compound");

   
   private final Set<IndexType> types;
   private final String[] parents;
   private final String name;
   
   private IndexType(String name, String... parents) {
      this.types = new HashSet<IndexType>();
      this.parents = parents;
      this.name = name;
   }
   
   public String getName() {
      return name;
   }
   
   public boolean isRoot() {
      return this == SCRIPT;
   }
   
   public boolean isImport() {
      return this == IMPORT;
   }
   
   public boolean isClass(){
      return this == CLASS;
   }

   public boolean isTrait(){
      return this == TRAIT;
   }
   
   public boolean isSuper() {
      return this == SUPER;
   }
   
   public boolean isProperty(){
      return this == PROPERTY;
   }
   
   public boolean isType() {
      return this == CLASS ||
              this == ENUM ||
              this == TRAIT ||
              this == MODULE ||
              this == IMPORT;
   }
   
   public boolean isMember() {
      return this == FUNCTION ||
            this == CONSTRUCTOR ||
            this == MEMBER_FUNCTION ||
            this == PROPERTY;
   }
   
   public boolean isConstructor(){
      return this == CONSTRUCTOR;
   }

   public boolean isMemberFunction(){
      return this == MEMBER_FUNCTION;
   }
   
   public boolean isFunction(){
      return this == FUNCTION ||
              this == CONSTRUCTOR ||
              this == MEMBER_FUNCTION;
   }
   
   public boolean isCompound(){
      return this == COMPOUND;
   }
   
   public boolean isConstrained() {
      return this == PARAMETER ||
              this == PROPERTY ||
              this == VARIABLE ||
              this == FUNCTION ||
              this == MEMBER_FUNCTION;
   }

   public boolean isLeaf() {
      return this == PARAMETER ||
              this == VARIABLE ||
              this == PROPERTY ||
              this == SUPER ||
              this == IMPORT;
   }
   
   public Set<IndexType> getParentTypes() {
      if(types.isEmpty()) {
         for(int i = 0; i < parents.length; i++) {
            IndexType type = TYPES.get(parents[i]);
         
            if(type == null) {
               throw new IllegalStateException("Invalid index " + parents[i] + " for " + this);
            }
            types.add(type);
         }
      }
      return types;
   }
   
   private static final Map<String, IndexType> TYPES = new LinkedHashMap<String, IndexType>();
   
   static {
      IndexType[] types = IndexType.values();
      
      for(IndexType type : types) {
         TYPES.put(type.name, type);
      }
   }
}
