package org.ternlang.studio.service.json.document;

public interface DocumentAssembler {
   void begin();
   void name(char[] source, int off, int length);
   void text(char[] source, int off, int length);
   void decimal(char[] source, int off, int length);
   void integer(char[] source, int off, int length);
   void bool(char[] source, int off, int length);
   void none(char[] source, int off, int length);
   void blockBegin();
   void blockEnd();
   void arrayBegin();
   void arrayEnd();
   void end();
}
