package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.ternlang.studio.common.json.document.TextTrie;
import org.ternlang.studio.common.json.entity.Entity;
import org.ternlang.studio.common.json.entity.EntityProvider;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class ClassProvider implements EntityProvider {

   private final TextTrie<ClassEntity> index;
   private final PropertyConverter converter;
   private final ObjectBuilder builder;
   
   public ClassProvider(ObjectBuilder builder, PropertyConverter converter) {
      this.index = new TextTrie<ClassEntity>();
      this.converter = converter;
      this.builder = builder;
   }
   
   @Override
   public Object getInstance(CharSequence type) {
      return builder.create(type);
   }

   @Override
   public Entity getEntity(CharSequence type) {
      return index.match(type);
   }

   public ClassEntity index(Class type) {
      String name = type.getSimpleName();
      ClassEntity tree = index.match(name);
      
      if(tree == null) {
         ClassEntity create = new ClassEntity(builder, converter, type, name);
         Set<Class> types = new HashSet<Class>();
         
         builder.index(type);
         index.index(create, name);
         index(type, create, types);
         
         return create;
      }
      return tree;
   }
   
   private void index(Class type, ClassEntity tree, Set<Class> done) {      
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