package org.ternlang.studio.resource.action.write;

import java.util.Collections;
import java.util.List;

import org.ternlang.studio.resource.action.Context;

public class ResponseWriter {

   private final List<BodyWriter> builders;

   public ResponseWriter() {
      this(Collections.EMPTY_LIST);
   }

   public ResponseWriter(List<BodyWriter> builders) {
      this.builders = builders;
   }

   public boolean write(Context context, Object result) throws Exception {
      for (BodyWriter builder : builders) {
         if (builder.accept(context, result)) {
            builder.write(context, result);
            return true;
         }
      }
      return false;
   }
}
