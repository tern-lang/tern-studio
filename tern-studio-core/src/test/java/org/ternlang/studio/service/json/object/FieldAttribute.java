package org.ternlang.studio.service.json.object;

import org.ternlang.studio.service.json.handler.BooleanValue;
import org.ternlang.studio.service.json.handler.DecimalValue;
import org.ternlang.studio.service.json.handler.Document.Attribute;
import org.ternlang.studio.service.json.handler.Document.Element;
import org.ternlang.studio.service.json.handler.IntegerValue;
import org.ternlang.studio.service.json.handler.NullValue;
import org.ternlang.studio.service.json.handler.TextValue;

public class FieldAttribute implements Attribute {

   private TokenConverter converter;
   private FieldAccessor accessor;
   private Object object;
   
   public FieldAttribute(TokenConverter converter) {
      this.converter = converter;
   }
   
   public FieldAttribute with(FieldAccessor accessor, Object object) {
      this.accessor = accessor;
      this.object = object;
      return this;
   }

   @Override
   public void set(TextValue value) {
      Class type = accessor.getType();
      CharSequence token = value.toToken();
      Object converted = converter.convert(type, token);
      
      accessor.setValue(object, converted);
   }

   @Override
   public void set(IntegerValue value) {
      Class type = accessor.getType();
      CharSequence token = value.toToken();
      Object converted = converter.convert(type, token);
      
      accessor.setValue(object, converted);
   }

   @Override
   public void set(DecimalValue value) {
      Class type = accessor.getType();
      CharSequence token = value.toToken();
      Object converted = converter.convert(type, token);
      
      accessor.setValue(object, converted);
   }

   @Override
   public void set(BooleanValue value) {
      Class type = accessor.getType();
      CharSequence token = value.toToken();
      Object converted = converter.convert(type, token);
      
      accessor.setValue(object, converted);
   }

   @Override
   public void set(NullValue value) {
      Class type = accessor.getType();
      CharSequence token = value.toToken();
      Object converted = converter.convert(type, token);
      
      accessor.setValue(object, converted);
   }

   @Override
   public void set(Element element) {
      Object value = element.get();
      accessor.setValue(object, value);
   }

   @Override
   public void reset() {
      object = null;
      accessor = null;
   }
}
