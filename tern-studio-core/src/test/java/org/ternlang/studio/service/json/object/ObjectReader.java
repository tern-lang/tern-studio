package org.ternlang.studio.service.json.object;

import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.common.ArrayStack;
import org.ternlang.studio.service.json.JsonParser;
import org.ternlang.studio.service.json.document.DocumentAssembler;
import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.Name;
import org.ternlang.studio.service.json.document.Value;

public class ObjectReader {

   private final DocumentAssembler assembler;
   private final ObjectHandler handler;
   private final JsonParser parser;
   
   public ObjectReader(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, String root, String type) {
      this.handler = new ObjectHandler(indexer, converter, builder, root);
      this.assembler = new TypeAssembler(handler, type);
      this.parser = new JsonParser(assembler);
   }
   
   public <T> T read(String source) throws Exception {
      parser.parse(source);
      return (T)handler.get();
   }

   private final class ObjectHandler implements DocumentHandler {
      
      private final AtomicReference<Object> reference;
      private final ArrayStack<FieldElement> stack;
      private final ArrayStack<Object> objects;
      private final ValueConverter converter;
      private final ObjectBuilder builder;
      private final TypeIndexer indexer;
      private final String root;
      
      public ObjectHandler(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, String root) {
         this.reference = new AtomicReference<Object>();
         this.stack = new ArrayStack<FieldElement>();
         this.objects = new ArrayStack<Object>();
         this.converter = converter;
         this.indexer = indexer;
         this.builder = builder;
         this.root = root;
      }
      
      public Object get() {
         return reference.get();
      }
      
      @Override
      public void begin() {
         objects.clear();
         stack.clear();
      }
      
      @Override
      public void attribute(Name name, Value value) {
         if(!name.isEmpty()) {
            CharSequence token = name.toText();
            FieldElement top = stack.peek();
            Object object = objects.peek();
            
            if(top == null) {
               throw new IllegalStateException("Attribute '" + name + "' has not block");
            }
            FieldAttribute field = top.attribute(token);
            
            if(field != null) {
               Class type = field.getType();

               if (!type.isArray()) {
                  Object converted = converter.convert(type, value);
                  field.setValue(object, converted);
               }
            }
         }
      }

      @Override
      public void blockBegin(Name name) {
         if(!name.isEmpty()) {
            CharSequence token = name.toText();
            FieldElement top = stack.peek();
            Object object = objects.peek();

            if(top == null) {
               throw new IllegalStateException("Illegal JSON ending");
            }
            FieldAttribute field = top.attribute(token);

            if(field != null) {
               String type = field.getName();
               FieldElement element = indexer.match(type);
               Object value = builder.create(type);

               objects.push(value);
               stack.push(element);
               field.setValue(object, value);
            }
         } else {
            FieldElement element = indexer.match(root);
            Object value = builder.create(root);

            objects.push(value);
            stack.push(element);
         }
      }

      @Override
      public void blockBegin(Name name, Name override) {
         CharSequence type = override.toText();
         Object value = builder.create(type);

         if(!name.isEmpty()) {
            CharSequence token = name.toText();
            FieldElement top = stack.peek();
            FieldAttribute field = top.attribute(token);

            if(field != null) {
               FieldElement element = indexer.match(type);
               Object object = objects.peek();

               if(top == null) {
                  throw new IllegalStateException("Illegal JSON ending");
               }
               objects.push(value);
               stack.push(element);
               field.setValue(object, value);
            }
         } else {
            FieldElement element = indexer.match(type);

            objects.push(value);
            stack.push(element);
         }
      }
      
      @Override
      public void blockEnd() {
         Object value = objects.pop();
         
         reference.set(value);
         stack.pop();
      }
      
      @Override
      public void arrayBegin(Name name) {
         
      }
      
      @Override
      public void arrayEnd() {
         
      }
      
      @Override
      public void end() {
         if(!stack.isEmpty()) {
            throw new IllegalStateException("Illegal JSON ending");
         }
      }
   }
}
