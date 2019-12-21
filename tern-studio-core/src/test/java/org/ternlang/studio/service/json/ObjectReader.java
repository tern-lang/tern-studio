package org.ternlang.studio.service.json;

import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.common.ArrayStack;
import org.ternlang.studio.service.json.handler.AttributeHandler;
import org.ternlang.studio.service.json.handler.BooleanValue;
import org.ternlang.studio.service.json.handler.DecimalValue;
import org.ternlang.studio.service.json.handler.IntegerValue;
import org.ternlang.studio.service.json.handler.Name;
import org.ternlang.studio.service.json.handler.NullValue;
import org.ternlang.studio.service.json.handler.TextValue;

public class ObjectReader {
   
   private final ObjectHandler handler;
   private final JsonAssembler assembler;
   private final JsonParser parser;
   
   public ObjectReader(FieldTree tree) {
      this.handler = new ObjectHandler(tree);
      this.assembler = new DirectAssembler(handler);
      this.parser = new JsonParser(assembler);
   }
   
   public <T> T read(String source) throws Exception {
      parser.parse(source);
      return (T)handler.getObject();
   }
   
   
   private final class ObjectHandler implements AttributeHandler {
      
      private final AtomicReference<Object> reference;
      private final ArrayStack<FieldTree> stack;
      private final ArrayStack<Object> objects;
      private final TokenConverter converter;
      private final FieldTree root;
      
      public ObjectHandler(FieldTree root) {
         this.reference = new AtomicReference<Object>();
         this.converter = new TokenConverter();
         this.stack = new ArrayStack<FieldTree>();
         this.objects = new ArrayStack<Object>();
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
      public void onAttribute(Name name, TextValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, IntegerValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, DecimalValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, BooleanValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, NullValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }    
     
      private void onAttribute(Name name, CharSequence value) {
         if(!name.isEmpty()) {
            CharSequence token = name.toToken();
            FieldTree top = stack.peek();
            Object object = objects.peek();
            
            if(top == null) {
               throw new IllegalStateException("Attribute '" + name + "' has not block");
            }
            FieldAccessor field = top.getAttribute(token);
            
            if(field == null) {
               throw new IllegalStateException("Could not find '" + name + "'");
            }
            Class type = field.getType();
            Object converted = converter.convert(type, value);
           
            field.setValue(object, converted);
         }
      }
      
      @Override
      public void onBlockBegin(Name name, Name type) {
         onBlockBegin(name);
      }
      
      @Override
      public void onBlockBegin(Name name) {
         if(!name.isEmpty()) {
            CharSequence token = name.toToken();
            FieldTree top = stack.peek();
            Object object = objects.peek();
            
            if(top == null) {
               throw new IllegalStateException("Illegal JSON ending");
            }
            FieldAccessor field = top.getAttribute(token);
            FieldTree child = top.getChild(token);
            Object value = child.getInstance();
            
            objects.push(value);
            stack.push(child);
            field.setValue(object, value);
         } else {
            Object object = root.getInstance();
            
            objects.push(object);
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
