package org.ternlang.studio.service.json.handler;

public interface AttributeHandler {
   void onBegin();
   void onBlockBegin(Name name);
   void onBlockBegin(Name name, Name type);
   void onArrayBegin(Name name);
   void onAttribute(Name name, TextValue value);
   void onAttribute(Name name, IntegerValue value);
   void onAttribute(Name name, DecimalValue value);
   void onAttribute(Name name, BooleanValue value);
   void onAttribute(Name name, NullValue value);
   void onBlockEnd();
   void onArrayEnd();
   void onEnd();
}