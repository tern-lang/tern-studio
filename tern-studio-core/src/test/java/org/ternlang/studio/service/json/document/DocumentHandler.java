package org.ternlang.studio.service.json.document;

public interface DocumentHandler {
   void begin();
   void blockBegin(Name name);
   void blockBegin(Name name, Name type);
   void arrayBegin(Name name);
   void attribute(Name name, Value value);
   void blockEnd();
   void arrayEnd();
   void end();
}