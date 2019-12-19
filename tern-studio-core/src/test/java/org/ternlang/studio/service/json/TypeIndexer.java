package org.ternlang.studio.service.json;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

class TypeIndexer {
   
   private final Set<Field> fields;
   private final Set<Class> types;
   private final FieldTree tree;
   
   public TypeIndexer(Constructor factory) {
      this.tree = new FieldTree(factory);
      this.fields = new HashSet<Field>();
      this.types = new HashSet<Class>();
   }

   public FieldTree index() {
      Class type = tree.getType();
      
      index(tree, type);
      return tree;
   }
   
   private void index(FieldTree tree, Class type) {      
      if(!types.add(type)) {
         throw new IllegalStateException("Cycle in type schema of " + type);
      }
      Class base = type;
      
      while(base != null) {
         Field[] list = base.getDeclaredFields();
         
         for(Field field : list) {
            if(fields.add(field)) {
               String name = field.getName();
               Class declared = field.getType();
               
               field.setAccessible(true);
               
               if(leaf(declared)) {
                  tree.addAttribute(name, field);
               } else {
                  FieldTree child = tree.addChild(name, field);
                  index(child, declared);
               }               
            }
         }
         base = base.getSuperclass();
      }
   }
   
   private boolean leaf(Class actual) {
      if(actual.isPrimitive()) {
         return true;
      }
      if(actual == String.class) {
         return true;
      }
      if(actual == Integer.class) {
         return true;
      }
      if(actual == Double.class) {
         return true;
      }
      if(actual == Float.class) {
         return true;
      }
      if(actual == Boolean.class) {
         return true;
      }
      if(actual == Byte.class) {
         return true;
      }
      if(actual == Short.class) {
         return true;
      }
      if(actual == Long.class) {
         return true;
      }
      if(actual == Character.class) {
         return true;
      }
      if(actual == File.class) {
         return true;
      }
      if(actual == URI.class) {
         return true;
      }
      if(actual == Class.class) {
         return true;
      }
      if(Enum.class.isAssignableFrom(actual)) {
         return true;
      }
      return false;
   }
}