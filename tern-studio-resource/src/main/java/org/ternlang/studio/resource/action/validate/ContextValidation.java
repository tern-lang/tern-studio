package org.ternlang.studio.resource.action.validate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.Interpolator;

public class ContextValidation implements Validation {

   private final Interpolator interpolator;
   private final Set<String> errors;

   public ContextValidation(Context context) {
      this.interpolator = new Interpolator(context);
      this.errors = new HashSet<String>();
   }

   @Override
   public boolean isValid() {
      return errors.isEmpty();
   }

   @Override
   public Iterator<String> iterator() {
      return errors.iterator();
   }

   @Override
   public void addError(String error) {
      String message = interpolator.interpolate(error);

      if (message != null) {
         errors.add(message);
      }
   }

   @Override
   public Set<String> getErrors() {
      return errors;
   }
}
