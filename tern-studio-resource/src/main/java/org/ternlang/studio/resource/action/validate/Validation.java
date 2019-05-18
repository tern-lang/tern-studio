package org.ternlang.studio.resource.action.validate;

import java.util.Set;

public interface Validation extends Iterable<String> {
   boolean isValid();
   Set<String> getErrors();
   void addError(String error);
}
