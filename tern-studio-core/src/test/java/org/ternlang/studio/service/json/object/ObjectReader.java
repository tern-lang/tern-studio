package org.ternlang.studio.service.json.object;

import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.common.ArrayStack;
import org.ternlang.studio.service.json.JsonParser;
import org.ternlang.studio.service.json.document.DirectAssembler;
import org.ternlang.studio.service.json.document.DocumentAssembler;
import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.Name;
import org.ternlang.studio.service.json.document.Value;

public class ObjectReader {
   
   private final ObjectHandler handler;
   private final DocumentAssembler assembler;
   private final JsonParser parser;
   
   public ObjectReader(FieldElement tree, ValueConverter converter, ObjectBuilder builder) {
      this.handler = new ObjectHandler(tree, converter, builder);
      this.assembler = new DirectAssembler(handler);
      this.parser = new JsonParser(assembler);
   }
   
   public <T> T read(String source) throws Exception {
      parser.parse(source);
      return (T)handler.getObject();
   }
   
   
   private final class ObjectHandler implements DocumentHandler {
      
      private final AtomicReference<Object> reference;
      private final ArrayStack<FieldElement> stack;
      private final ArrayStack<Object> objects;
      private final ValueConverter converter;
      private final ObjectBuilder builder;
      private final FieldElement root;
      
      public ObjectHandler(FieldElement root, ValueConverter converter, ObjectBuilder builder) {
         this.reference = new AtomicReference<Object>();
         this.stack = new ArrayStack<FieldElement>();
         this.objects = new ArrayStack<Object>();
         this.converter = converter;
         this.builder = builder;
         this.root = root;
      }
      
      public Object getObject() {
         return reference.get();
      }
      
      @Override
      public void onBegin() {
         objects.clear();
         stack.clear();
      }
      
      @Override
      public void onAttribute(Name name, Value value) {
         if(!name.isEmpty()) {
            CharSequence token = name.toToken();
            FieldElement top = stack.peek();
            Object object = objects.peek();
            
            if(top == null) {
               throw new IllegalStateException("Attribute '" + name + "' has not block");
            }
            FieldAttribute field = top.attribute(token);
            
            if(field == null) {
               throw new IllegalStateException("Could not find '" + name + "'");
            }
            Class type = field.getType();
            Object converted = converter.convert(type, value);
           
            field.setValue(object, converted);
         }
      }
      
      @Override
      public void onBlockBegin(Name name, Name override) {
         CharSequence type = override.toToken();
         Object value = builder.create(type);
         
         if(!name.isEmpty()) {
            CharSequence token = name.toToken();
            FieldElement top = stack.peek();
            Object object = objects.peek();
            
            if(top == null) {
               throw new IllegalStateException("Illegal JSON ending");
            }
            FieldAttribute field = top.attribute(token);
            FieldElement child = top.element(token);
            
            objects.push(value);
            stack.push(child);
            field.setValue(object, value);
         } else {
            objects.push(value);
            stack.push(root);
         }
      }
      
      @Override
      public void onBlockBegin(Name name) {
         if(!name.isEmpty()) {
            CharSequence token = name.toToken();
            FieldElement top = stack.peek();
            Object object = objects.peek();
            
            if(top == null) {
               throw new IllegalStateException("Illegal JSON ending");
            }
            FieldAttribute field = top.attribute(token);
            String type = field.getName();
            FieldElement child = top.element(type);
            Object value = builder.create(type);
            
            objects.push(value);
            stack.push(child);
            field.setValue(object, value);
         } else {
            String type = root.getType();
            Object value = builder.create(type);
            
            objects.push(value);
            stack.push(root);
         }
      }
      
      @Override
      public void onBlockEnd() {
         Object value = objects.pop();
         
         reference.set(value);
         stack.pop();
      }
      
      @Override
      public void onArrayBegin(Name name) {
         
      }
      
      @Override
      public void onArrayEnd() {
         
      }
      
      @Override
      public void onEnd() {
         if(!stack.isEmpty()) {
            throw new IllegalStateException("Illegal JSON ending");
         }
      }
   }
}
