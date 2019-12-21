package org.ternlang.studio.service.json.handler;

import java.util.List;

import org.ternlang.studio.service.json.operation.Type;

public interface Document {
   Object get();
   Element element();
   ElementList list();
   void reset();
   
   interface Element {
      Object get();
      Attribute attribute(Name name);
      Element element(Name name);
      Element element(Name name, Type type);
      ElementList list(Name name);
      void reset();
   }
   
   interface ElementList {
      List<?> get();
      Element element();
      Element element(Type type);
      void reset();
   }
   
   interface Attribute {
      void set(TextValue value);
      void set(IntegerValue value);
      void set(DecimalValue value);
      void set(BooleanValue value);
      void set(NullValue value);
      void set(Element element);
      void reset();
   }
}
