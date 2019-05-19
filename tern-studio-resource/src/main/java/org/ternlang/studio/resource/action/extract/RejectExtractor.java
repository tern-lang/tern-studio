package org.ternlang.studio.resource.action.extract;

import org.ternlang.studio.resource.action.Context;

public class RejectExtractor implements Extractor<Object> {
   
   private final Class parent;
   private final Class type;
   
   public RejectExtractor(Class parent, Class type) {
      this.parent = parent;
      this.type = type;
   }

   @Override
   public Object extract(Parameter parameter, Context context) throws Exception {
      throw new IllegalStateException("Could not resolve " + type + " for " + parent);
   }

   @Override
   public boolean accept(Parameter parameter) throws Exception {
      return false;
   }

}
