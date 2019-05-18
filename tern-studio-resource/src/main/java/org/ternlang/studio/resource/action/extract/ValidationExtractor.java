package org.ternlang.studio.resource.action.extract;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.validate.Validation;

public class ValidationExtractor implements Extractor<Validation> {

   @Override
   public Validation extract(Parameter parameter, Context context) {
      Validation validation = context.getValidation();
      Class type = parameter.getType();

      if (type == Validation.class) {
         return validation;
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class type = parameter.getType();

      if (type == Validation.class) {
         return true;
      }
      return false;
   }
}
