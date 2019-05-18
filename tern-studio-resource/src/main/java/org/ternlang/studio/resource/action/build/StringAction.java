package org.ternlang.studio.resource.action.build;

import org.ternlang.studio.resource.action.Action;
import org.ternlang.studio.resource.action.Context;

public class StringAction implements Action {
   
   private final String name;
   
   public StringAction(String name) {
      this.name = name;
   }

   @Override
   public Object execute(Context context) {
      return name;
   }

}
