package org.ternlang.studio.index.complete;

public class EditContext {
   
   private final String expression; // formatted expression
   private final String original; // original expression;
   private final String source;
   
   public EditContext(String source, String original, String expression) {
      this.source = source;
      this.original = original;
      this.expression = expression;
   }
   
   public String getSource() {
      return source;
   }
   
   public String getExpression(){
      return expression;
   }
   
   public String getOriginalExpression() {
      return original;
   }
   
}