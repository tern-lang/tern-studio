package org.ternlang.studio.service.json;

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
   private final JsonParser parser;
   
   public ObjectReader(FieldTree tree) {
      this.handler = new ObjectHandler(tree);
      this.parser = new JsonParser(handler);
   }
   
   public <T> T read(String source) throws Exception {
      parser.parse(source);
      return (T)handler.getObject();
   }
   
   
   private final class ObjectHandler implements AttributeHandler {
      
      private final TokenConverter converter;
      private final ArrayStack<FieldTree> stack;
      private final ArrayStack<Object> objects;
      private final FieldTree root;
      
      public ObjectHandler(FieldTree root) {
         this.converter = new TokenConverter();
         this.stack = new ArrayStack<FieldTree>();
         this.objects = new ArrayStack<Object>();
         this.root = root;
      }
      
      public Object getObject() {
         return objects.peek();
      }
      
      @Override
      public void onBegin() {
         Object object = root.getInstance();
         
         objects.clear();
         objects.push(object);
         stack.clear();
         stack.push(root);
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
         if(name != null) {
            CharSequence token = name.toToken();
            FieldTree top = stack.peek();
            Object object = objects.peek();
            
            if(top == null) {
               throw new IllegalStateException("Illegal JSON ending");
            }
            FieldAccessor field = top.getAttribute(token);
            
            if(field == null) {
               throw new IllegalStateException("Could not find " + name);
            }
            Class type = field.getType();
            Object converted = converter.convert(type, value);
           
            field.setValue(object, converted);
         }
      }
      
      @Override
      public void onBlockBegin(Name name) {
         if(name != null) {
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
         }
      }
      
      @Override
      public void onBlockEnd(Name name) {
         if(name != null) {
            objects.pop();
            stack.pop();
         }
      }
      
      @Override
      public void onArrayBegin(Name name) {
         
      }
      
      @Override
      public void onArrayEnd(Name name) {
         
      }
      
      @Override
      public void onEnd() {
         FieldTree top = stack.peek();
         
         if(top != root) {
            throw new IllegalStateException("Illegal JSON ending");
         }
      }
      
   }
}
