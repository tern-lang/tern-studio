package org.ternlang.studio.index.complete;

public class InputExpression {

   private final String handle;
   private final String unfinished;
   
   public InputExpression(String handle, String unfinished) {
      this.handle = handle;
      this.unfinished = unfinished;
   }

   public String getHandle() {
      return handle;
   }

   public String getUnfinished() {
      return unfinished;
   }
   
   
}
