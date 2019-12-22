package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.ternlang.studio.common.json.document.TextTrie;

public class TypeIndexer {

   private final TextTrie<FieldElement> index;
   private final ValueConverter converter;
   private final ObjectBuilder builder;
   
   public TypeIndexer(ValueConverter converter, ObjectBuilder builder) {
      this.index = new TextTrie<FieldElement>();
      this.converter = converter;
      this.builder = builder;
   }
   
   public FieldElement match(CharSequence type) {
      return index.match(type);
   }

   public FieldElement index(Class type) {
      String name = type.getSimpleName();
      FieldElement tree = index.match(name);
      
      if(tree == null) {
         FieldElement create = new FieldElement(name);
         Set<Class> types = new HashSet<Class>();
         
         builder.index(type);
         index.index(create, name);
         index(type, create, types);
         
         return create;
      }
      return tree;
   }
   
   private void index(Class type, FieldElement tree, Set<Class> done) {      
      if(!done.add(type)) {
         throw new IllegalStateException("Cycle in type schema of " + type);
      }
      Class base = type;
      
      while(base != null) {
         Field[] list = base.getDeclaredFields();
         
         for(Field field : list) {
            String name = field.getName();
            Class declared = field.getType();
            
            field.setAccessible(true);
            
            if(!converter.accept(declared)) {
               if(!declared.isArray()) {
                  index(declared);
               }
            }
            tree.index(name, field);             
         }
         base = base.getSuperclass();
      }
   }
}