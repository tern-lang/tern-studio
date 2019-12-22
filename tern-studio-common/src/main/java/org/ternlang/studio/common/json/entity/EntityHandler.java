package org.ternlang.studio.common.json.entity;

import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.common.ArrayStack;
import org.ternlang.studio.common.json.document.DocumentHandler;
import org.ternlang.studio.common.json.document.Name;
import org.ternlang.studio.common.json.document.Value;

class EntityHandler implements DocumentHandler {
   
   private final AtomicReference<Object> reference;
   private final ArrayStack<Entity> entities;
   private final ArrayStack<Object> objects;
   private final EntityProvider provider;
   private final Name root;
   
   public EntityHandler(EntityProvider provider, Name root) {
      this.reference = new AtomicReference<Object>();
      this.entities = new ArrayStack<Entity>();
      this.objects = new ArrayStack<Object>();
      this.provider = provider;
      this.root = root;
   }
   
   public Object get() {
      return reference.get();
   }
   
   @Override
   public void begin() {
      objects.clear();
      entities.clear();
   }
   
   @Override
   public void attribute(Name name, Value value) {
      if(!name.isEmpty()) {
         CharSequence token = name.toText();
         Entity entity = entities.peek();
         
         if(entity == null) {
            throw new IllegalStateException("Attribute '" + name + "' has not block");
         }
         Object object = objects.peek();
         Property property = entity.getProperty(token);
         
         if(property != null) {
            property.setValue(object, value);
         }
      }
   }

   @Override
   public void blockBegin(Name name) {
      if(!name.isEmpty()) {
         CharSequence token = name.toText();
         Entity entity = entities.peek();

         if(entity == null) {
            throw new IllegalStateException("Illegal JSON ending");
         }
         Object object = objects.peek();
         Property property = entity.getProperty(token);

         if(property != null) {
            String type = property.getName();
            Entity element = provider.getEntity(type);
            Object value = provider.getInstance(type);

            objects.push(value);
            entities.push(element);
            property.setValue(object, value);
         }
      } else {
         CharSequence type = root.toText();
         Entity entity = provider.getEntity(type);
         Object value = provider.getInstance(type);

         objects.push(value);
         entities.push(entity);
      }
   }

   @Override
   public void blockBegin(Name name, Name override) {
      CharSequence type = override.toText();
      Object value = provider.getInstance(type);

      if(!name.isEmpty()) {
         CharSequence token = name.toText();
         Entity entity = entities.peek();
         
         if(entity == null) {
            throw new IllegalStateException("Illegal JSON ending");
         }
         Property property = entity.getProperty(token);

         if(property != null) {
            Entity child = provider.getEntity(type);
            Object object = objects.peek();

            objects.push(value);
            entities.push(child);
            property.setValue(object, value);
         }
      } else {
         Entity entity = provider.getEntity(type);

         objects.push(value);
         entities.push(entity);
      }
   }
   
   @Override
   public void blockEnd() {
      Object value = objects.pop();
      
      reference.set(value);
      entities.pop();
   }
   
   @Override
   public void arrayBegin(Name name) {
      
   }
   
   @Override
   public void arrayEnd() {
      
   }
   
   @Override
   public void end() {
      if(!entities.isEmpty()) {
         throw new IllegalStateException("Illegal JSON ending");
      }
   }
}