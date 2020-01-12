package org.ternlang.studio.message;

public class ByteArrayFrame implements Frame {

   private byte[] array;
   private int count;

   public ByteArrayFrame() {
      this.array = new byte[] {};
   }

   @Override
   public boolean getBoolean(int offset) {
      return array[offset] == 1;
   }

   @Override
   public ByteArrayFrame setBoolean(int offset, boolean value) {
      ensureCount(offset + ByteSize.BOOL_SIZE);
      array[offset] = (byte) (value ? 1 : 0);
      return this;
   }

   @Override
   public byte getByte(int offset) {
      return array[offset];
   }

   @Override
   public ByteArrayFrame setByte(int offset, byte value) {
      ensureCount(offset + ByteSize.BYTE_SIZE);
      array[offset] = value;
      return this;
   }

   @Override
   public short getShort(int offset) {
      short value = 0;
      
      value |= (short)(array[offset]& 0xff) << 8;
      value |= (short)(array[offset + 1] & 0xff);
      
      return value;
   }

   @Override
   public ByteArrayFrame setShort(int offset, short value) {
      ensureCount(offset + ByteSize.SHORT_SIZE);
      array[offset] = (byte) (value >>> 8);
      array[offset + 1] = (byte) (value);
      return this;
   }

   @Override
   public int getInt(int offset) {
      int value = 0;
      
      value |= array[offset] << 24;
      value |= (array[offset + 1] & 0xff) << 16;
      value |= (array[offset + 2] & 0xff) << 8; 
      value |= (array[offset + 3] & 0xff);
      
      return value;
   }

   @Override
   public ByteArrayFrame setInt(int offset, int value) {
      ensureCount(offset + ByteSize.INT_SIZE);
      array[offset] = (byte) (value >>> 24);
      array[offset + 1] = (byte) (value >>> 16);
      array[offset + 2] = (byte) (value >>> 8);
      array[offset + 3] = (byte) (value);
      return this;
   }

   @Override
   public long getLong(int offset) {
      long value = 0;
      
      value |= ((long)array[offset]) << 56;
      value |= ((long)array[offset + 1] & 0xff) << 48;
      value |= ((long)array[offset + 2] & 0xff) << 40;
      value |= ((long)array[offset + 3] & 0xff) << 32;
      value |= ((long)array[offset + 4] & 0xff) << 24;
      value |= ((long)array[offset + 5] & 0xff) << 16;
      value |= ((long)array[offset + 6] & 0xff) << 8;
      value |= ((long)array[offset + 7] & 0xff);
      
      return value;
   }

   @Override
   public ByteArrayFrame setLong(int offset, long value) {
      ensureCount(offset + ByteSize.LONG_SIZE);
      array[offset] = (byte) (value >>> 56);
      array[offset + 1] = (byte) (value >>> 48);
      array[offset + 2] = (byte) (value >>> 40);
      array[offset + 3] = (byte) (value >>> 32);
      array[offset + 4] = (byte) (value >>> 24);
      array[offset + 5] = (byte) (value >>> 16);
      array[offset + 6] = (byte) (value >>> 8);
      array[offset + 7] = (byte) (value);
      return this;
   }

   @Override
   public double getDouble(int offset) {
      return Double.longBitsToDouble(getLong(offset));
   }

   @Override
   public ByteArrayFrame setDouble(int offset, double value) {
      setLong(offset, Double.doubleToLongBits(value));
      return this;
   }

   @Override
   public float getFloat(int offset) {
      return Float.intBitsToFloat(getInt(offset));
   }

   @Override
   public ByteArrayFrame setFloat(int offset, float value) {
      setInt(offset, Float.floatToIntBits(value));
      return this;
   }

   @Override
   public char getChar(int offset) {
      return (char) (array[offset] << 8 | array[offset + 1]);
   }

   @Override
   public ByteArrayFrame setChar(int offset, char value) {
      ensureCount(offset + ByteSize.CHAR_SIZE);
      array[offset] = (byte) (value >>> 8);
      array[offset + 1] = (byte) (value);
      return this;
   }

   private void ensureCount(int position) {
      if (array.length < position) {
         byte[] copy = new byte[position * 2];
         System.arraycopy(array, 0, copy, 0, count);
         array = copy;
      }
      if (count <= position) {
         count = position;
      }      
   }   
   
   public byte[] getByteArray() {
      return array;
   }
   
   public int capacity() {
      return array.length;
   }

   @Override
   public int length() {
      return count;
   }
}
