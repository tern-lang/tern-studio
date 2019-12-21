package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.Slice;
import org.ternlang.studio.service.json.document.Value;

public class Attribute extends Operation {
   
   private final OperationPool pool;
   private final DecimalValue decimal;
   private final IntegerValue integer;
   private final BooleanValue bool;
   private final TextValue text;
   private final NameSlice name;
   private final NullValue none;
   
   public Attribute(OperationPool pool) {
      this.integer = new IntegerValue();
      this.decimal = new DecimalValue();
      this.bool = new BooleanValue();
      this.text = new TextValue();
      this.name = new NameSlice();
      this.none = new NullValue();
      this.pool = pool;
   }

   @Override
   public void execute(DocumentHandler handler) {
      if(!text.isEmpty()) {
         handler.onAttribute(name, text);
      } else if(!bool.isEmpty()) {
         handler.onAttribute(name, bool);
      } else if(!decimal.isEmpty()) {
         handler.onAttribute(name, decimal);
      } else if(!integer.isEmpty()) {
         handler.onAttribute(name, integer);
      } else if(!none.isEmpty()) {
         handler.onAttribute(name, none);
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
   
   public void decimal(char[] source, int off, int length) {
      decimal.with(source, off, length);
   }
   
   public void integer(char[] source, int off, int length) {
      integer.with(source, off, length);
   }
   
   public void bool(char[] source, int off, int length) {
      bool.with(source, off, length);
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
      
      private final Slice slice;    
      
      public TextValue() {
         this.slice = new Slice();
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
         return slice.length() <= 0;
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
      
      private final Slice slice; 
      private boolean value;
      
      public BooleanValue() {
         this.slice = new Slice();
      }
      
      public BooleanValue with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }

      @Override
      public boolean toBoolean() {
         return value;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      @Override
      public void reset() {
         slice.reset();
         value = false;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class DecimalValue extends Value {
      
      private final Slice slice; 
      private double value;
      
      public DecimalValue() {
         this.slice = new Slice();
      }
      
      public DecimalValue with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }

      @Override
      public double toDouble() {
         return value;
      }

      @Override
      public float toFloat() {
         return (float)value;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      @Override
      public void reset() {
         slice.reset();
         value = 0;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class IntegerValue extends Value {
      
      private final Slice slice; 
      private long value;
      
      public IntegerValue() {
         this.slice = new Slice();
      }

      public IntegerValue with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return slice;
      }

      @Override
      public long toLong() {
         return value;
      }

      @Override
      public int toInteger() {
         return (int)value;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      @Override
      public void reset() {
         slice.reset();
         value = 0;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class NullValue extends Value {
      
      private final Slice slice;    
      
      public NullValue() {
         this.slice = new Slice();
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
         return slice.length() <= 0;
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
