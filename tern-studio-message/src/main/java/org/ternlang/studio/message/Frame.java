package org.ternlang.studio.message;

/*
 
 
 CharOption
 
 CharSequence
    ^
    |
 CharArray
    ^
    |
 CharArrayBuilder
 
 class Foo {
    CharArray name();
 }
 
 class FooBuilder implements Foo {
    
    @Override
    CharArrayBuilder name();
    FooBuilder name(CharSequence name);
 }
 


 */


public interface Frame {
   boolean getBoolean(int offset);
   byte getByte(int offset);
   short getShort(int offset);
   int getInt(int offset);
   long getLong(int offset);
   double getDouble(int offset);
   float getFloat(int offset);
   char getChar(int offset);
   int length();
}
