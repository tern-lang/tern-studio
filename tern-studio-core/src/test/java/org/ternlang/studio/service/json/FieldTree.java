package org.ternlang.studio.service.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class FieldTree {
   
   private final Trie<FieldAccessor> attributes;
   private final Trie<FieldTree> children;
   private final Set<String> names;
   private final Constructor factory;
   
   public FieldTree(Constructor factory) {
      this(factory, new HashSet<String>());
   }
   
   private FieldTree(Constructor factory, Set<String> names) {
      this.attributes = new Trie<FieldAccessor>();
      this.children = new Trie<FieldTree>();
      this.factory = factory;
      this.names = names;
   }
   
   public Set<String> literals() {
      return Collections.unmodifiableSet(names);
   }
   
   public Class getType() {
      return factory.getDeclaringClass();
   }
   
   public Object getInstance() {
      try {
         return factory.newInstance();
      } catch(Exception e) {
         throw new IllegalStateException("Could not instantiate", e);
      }
   }
   
   public FieldAccessor addAttribute(String name, Field field) {
      Class type = field.getType();
      FieldAccessor accessor = attributes.match(name);
      
      if(accessor == null) {
         accessor = new FieldAccessor(field);  
         
         field.setAccessible(true);
         names.add(name);
         attributes.index(accessor, name);
      }
      return accessor;
   }
   
   public FieldAccessor getAttribute(CharSequence name) {
      return attributes.match(name);
   }
   
   public FieldTree addChild(String name, Field field) {
      Class type = field.getType();
      FieldTree tree = children.match(name);
      
      if(tree == null) { 
         try {
            if(!type.isArray()) {
               Constructor constructor = type.getDeclaredConstructor();
               constructor.setAccessible(true);
               
               tree = new FieldTree(constructor, names);
            }
         } catch(Exception e) {
            throw new IllegalStateException("Could not create constructor for " + type, e);
         }
         names.add(name);
         children.index(tree, name);
         addAttribute(name, field);
      }
      return tree;
   }
   
   public FieldTree getChild(CharSequence name) {
      return children.match(name);
   }
}