package org.ternlang.studio.service.json.document;

public interface DocumentHandler {
   void onBegin();
   void onBlockBegin(Name name);
   void onBlockBegin(Name name, Name type);
   void onArrayBegin(Name name);
   void onAttribute(Name name, Value value);
   void onBlockEnd();
   void onArrayEnd();
   void onEnd();
}