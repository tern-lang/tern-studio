package org.ternlang.studio.common.json.operation;

import org.ternlang.studio.common.json.document.DocumentHandler;
import org.ternlang.studio.common.json.document.SourceSlice;
import org.ternlang.studio.common.json.document.Value;

public class Attribute extends Operation {
   
   private final OperationPool pool;
   private final DecimalValue decimal;
   private final IntegerValue integer;
   private final BooleanValue bool;
   private final TextValue text;
   private final NameValue name;
   private final NullValue none;
   
   public Attribute(OperationPool pool) {
      this.integer = new IntegerValue();
      this.decimal = new DecimalValue();
      this.bool = new BooleanValue();
      this.text = new TextValue();
      this.name = new NameValue();
      this.none = new NullValue();
      this.pool = pool;
   }

   @Override
   public void execute(DocumentHandler handler) {
      if(!text.isEmpty()) {
         handler.attribute(name, text);
      } else if(!bool.isEmpty()) {
         handler.attribute(name, bool);
      } else if(!decimal.isEmpty()) {
         handler.attribute(name, decimal);
      } else if(!integer.isEmpty()) {
         handler.attribute(name, integer);
      } else if(!none.isEmpty()) {
         handler.attribute(name, none);
      }
      if(pool != null) {
         pool.recycle(this);
      }
   }
   
   public void name(char[] source, int off, int length) {
      name.with(source, off, length);
   }
   
   public void text(char[] source, int off, int length) {
      text.with(source, off, length);
   }
   
   public void decimal(char[] source, int off, int length, double value) {
      decimal.with(source, off, length, value);
   }
   
   public void integer(char[] source, int off, int length, long value) {
      integer.with(source, off, length, value);
   }
   
   public void bool(char[] source, int off, int length, boolean value) {
      bool.with(source, off, length, value);
   }
   
   public void none(char[] source, int off, int length) {
      none.with(source, off, length);
   }
   
   @Override
   public void reset() {
      decimal.reset();
      integer.reset();
      name.reset();
      text.reset();
      bool.reset();
      none.reset();
   }

   private static class TextValue extends Value {
      
      private final SourceSlice slice;
      
      public TextValue() {
         this.slice = new SourceSlice();
      }
      
      public TextValue with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.isEmpty();
      }
      
      @Override
      public void reset() {
         slice.reset();
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class BooleanValue extends Value {
      
      private final SourceSlice slice;
      private boolean bool;
      
      public BooleanValue() {
         this.slice = new SourceSlice();
      }
      
      public BooleanValue with(char[] source, int off, int length, boolean value) {
         slice.with(source, off, length);
         bool = value;
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }

      @Override
      public boolean toBoolean() {
         return bool;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.isEmpty();
      }
      
      @Override
      public void reset() {
         slice.reset();
         bool = false;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class DecimalValue extends Value {
      
      private final SourceSlice slice;
      private double number;
      
      public DecimalValue() {
         this.slice = new SourceSlice();
      }
      
      public DecimalValue with(char[] source, int off, int length, double value) {
         slice.with(source, off, length);
         number = value;
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }

      @Override
      public double toDouble() {
         return number;
      }

      @Override
      public float toFloat() {
         return (float)number;
      }
      
      @Override
      public int toInteger() {
         return (int)number;
      }

      @Override
      public long toLong() {
         return (long)number;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.isEmpty();
      }
      
      @Override
      public void reset() {
         slice.reset();
         number = 0;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class IntegerValue extends Value {
      
      private final SourceSlice slice;
      private long number;
      
      public IntegerValue() {
         this.slice = new SourceSlice();
      }

      public IntegerValue with(char[] source, int off, int length, long value) {
         slice.with(source, off, length);
         number = value;
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }

      @Override
      public long toLong() {
         return number;
      }

      @Override
      public int toInteger() {
         return (int)number;
      }
      
      @Override
      public double toDouble() {
         return (double)number;
      }

      @Override
      public float toFloat() {
         return (float)number;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.isEmpty();
      }
      
      @Override
      public void reset() {
         slice.reset();
         number = 0;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class NullValue extends Value {
      
      private final SourceSlice slice;
      
      public NullValue() {
         this.slice = new SourceSlice();
      }
      
      public NullValue with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.isEmpty();
      }
      
      @Override
      public void reset() {
         slice.reset();
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
}
