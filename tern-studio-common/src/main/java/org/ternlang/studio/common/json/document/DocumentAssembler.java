package org.ternlang.studio.common.json.document;

public interface DocumentAssembler {
   void begin();
   void name(char[] source, int off, int length);
   void text(char[] source, int off, int length);
   void decimal(char[] source, int off, int length, double value);
   void integer(char[] source, int off, int length, long value);
   void bool(char[] source, int off, int length, boolean value);
   void none(char[] source, int off, int length);
   void blockBegin();
   void blockEnd();
   void arrayBegin();
   void arrayEnd();
   void end();
}
