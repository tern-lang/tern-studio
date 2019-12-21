package org.ternlang.studio.service.json.object;

import java.util.ArrayList;
import java.util.List;

import org.ternlang.studio.service.json.handler.Document;
import org.ternlang.studio.service.json.handler.Name;
import org.ternlang.studio.service.json.operation.Type;

public class ObjectDocument implements Document {
   
   private TokenConverter converter;
   private Object object;
   
   public ObjectDocument() {
      this.converter = new TokenConverter();
   }
   
   public ObjectDocument with(Object object) {
      this.object = object;
      return this;
   }

   @Override
   public Object get() {
      return object;
   }

   @Override
   public Element element() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ElementList list() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void reset() {
      // TODO Auto-generated method stub
      
   }

   
   private static class ObjectElement implements Element {

      private ObjectElement element;
      private FieldAttribute attribute;
      private List<Object> list;
      private FieldTree tree;
      private Object object;
      
      public ObjectElement(TokenConverter converter, FieldTree tree) {
         this.element = new ObjectElement(converter, tree);
         this.attribute = new FieldAttribute(converter);
         this.list = new ArrayList<Object>();
         this.tree = tree;
      }
      
      public ObjectElement with(Object object) {
         this.object = object;
         return this;
      }
      
      @Override
      public Object get() {
         return object;
      }

      @Override
      public Attribute attribute(Name name) {
         CharSequence token = name.toToken();
         FieldAccessor field = tree.getAttribute(token);
         
         if(field == null) {
            throw new IllegalStateException("Could not find '" + name + "'");
         }
         return attribute.with(field, object);
      }

      @Override
      public Element element(Name name) {
         CharSequence token = name.toToken();
         FieldAccessor field = tree.getAttribute(token);
         
         if(field == null) {
            throw new IllegalStateException("Could not find '" + name + "'");
         }
         Object instance = field.getInstance();
         field.setValue(object, instance);
         return element.with(instance);
      }

      @Override
      public Element element(Name name, Type type) {
         CharSequence token = name.toToken();
         FieldAccessor field = tree.getAttribute(token);
         
         if(field == null) {
            throw new IllegalStateException("Could not find '" + name + "'");
         }
         Object instance = field.getInstance();
         field.setValue(object, instance);
         return element.with(instance);
      }

      @Override
      public ElementList list(Name name) {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public void reset() {
         // TODO Auto-generated method stub
         
      }
      
   }


}
