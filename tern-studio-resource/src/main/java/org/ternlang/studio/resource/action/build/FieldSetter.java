package org.ternlang.studio.resource.action.build;

import java.lang.reflect.Field;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.extract.Extractor;

public class FieldSetter {

   private final Extractor extractor;
   private final Property property;
   private final Field field;

   public FieldSetter(Property property, Extractor extractor, Field field) {
      this.extractor = extractor;
      this.property = property;
      this.field = field;
   }

   public void set(Object object, Context context) throws Exception {
      Object value = extractor.extract(property, context);

      if (value == null) {
         if (property.isRequired()) {
            throw new IllegalStateException("Could not find value for " + property);
         }
      } else {
         field.set(object, value);
      }
   }

   public boolean valid(Context context) throws Exception {
      try {
         Object value = extractor.extract(property, context);

         if (value == null && property.isRequired()) {
            return false;
         }
      } catch (Exception e) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return field.toString();
   }
}
