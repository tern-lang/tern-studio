package org.ternlang.studio.message;

public interface Frame {
   boolean getBoolean(int offset);
   Frame setBoolean(int offset, boolean value);
   byte getByte(int offset);
   Frame setByte(int offset, byte value);
   short getShort(int offset);
   Frame setShort(int offset, short value);
   int getInt(int offset);
   Frame setInt(int offset, int value);
   long getLong(int offset);
   Frame setLong(int offset, long value);
   double getDouble(int offset);
   Frame setDouble(int offset, double value);
   float getFloat(int offset);
   Frame setFloat(int offset, float value);
   char getChar(int offset);
   Frame setChar(int offset, char value);
   int length();
}
