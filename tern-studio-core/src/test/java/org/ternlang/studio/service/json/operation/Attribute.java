package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.Slice;
import org.ternlang.studio.service.json.handler.AttributeHandler;
import org.ternlang.studio.service.json.handler.BooleanValue;
import org.ternlang.studio.service.json.handler.DecimalValue;
import org.ternlang.studio.service.json.handler.IntegerValue;
import org.ternlang.studio.service.json.handler.NullValue;
import org.ternlang.studio.service.json.handler.TextValue;

public class Attribute extends Operation {
   
   private final OperationPool pool;
   private final DecimalSlice decimal;
   private final IntegerSlice integer;
   private final BooleanSlice bool;
   private final TextSlice text;
   private final NameSlice name;
   private final NullSlice none;
   
   public Attribute(OperationPool pool) {
      this.integer = new IntegerSlice();
      this.decimal = new DecimalSlice();
      this.bool = new BooleanSlice();
      this.text = new TextSlice();
      this.name = new NameSlice();
      this.none = new NullSlice();
      this.pool = pool;
   }

   @Override
   public void execute(AttributeHandler handler) {
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

   private static class TextSlice implements TextValue {
      
      private final Slice slice = new Slice();    
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
      
      public TextSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      public void reset() {
         slice.reset();
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class BooleanSlice implements BooleanValue {
      
      private final Slice slice = new Slice(); 
      private boolean value;
      
      @Override
      public CharSequence toToken() {
         return slice;
      }

      public BooleanSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }

      @Override
      public boolean toBoolean() {
         return value;
      }
      
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      public void reset() {
         slice.reset();
         value = false;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class DecimalSlice implements DecimalValue {
      
      private final Slice slice = new Slice(); 
      private double value;
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
     
      public DecimalSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }

      @Override
      public double toDouble() {
         return value;
      }

      @Override
      public float toFloat() {
         return (float)value;
      }
      
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      public void reset() {
         slice.reset();
         value = 0;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class IntegerSlice implements IntegerValue {
      
      private final Slice slice = new Slice(); 
      private long value;
      
      @Override
      public CharSequence toToken() {
         return slice;
      }

      public IntegerSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }

      @Override
      public long toLong() {
         return value;
      }

      @Override
      public int toInteger() {
         return (int)value;
      }
      
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      public void reset() {
         slice.reset();
         value = 0;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class NullSlice implements NullValue {
      
      private final Slice slice = new Slice();    
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
      
      public NullSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      public void reset() {
         slice.reset();
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
}
