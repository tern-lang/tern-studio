package org.ternlang.studio.service.json.handler;

public interface AttributeHandler {
   void onBegin();
   void onAttribute(Name name, TextValue value);
   void onAttribute(Name name, IntegerValue value);
   void onAttribute(Name name, DecimalValue value);
   void onAttribute(Name name, BooleanValue value);
   void onAttribute(Name name, NullValue value);
   void onBlockBegin(Name name);
   void onBlockEnd(Name name);
   void onArrayBegin(Name name);
   void onArrayEnd(Name name);
   void onEnd();
}