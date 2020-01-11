package org.ternlang.studio.message;

public interface Frame {
   boolean getBoolean(int offset);
   void setBoolean(int offset, boolean value);
   byte getByte(int offset);
   void setByte(int offset, byte value);
   short getShort(int offset);
   void setShort(int offset, short value);
   int getInt(int offset);
   void setInt(int offset, int value);
   long getLong(int offset);
   void setLong(int offset, long value);
   double getDouble(int offset);
   void setDouble(int offset, double value);
   float getFloat(int offset);
   void setFloat(int offset, float value);
   char getChar(int offset);
   void setChar(int offset, char value);
}
