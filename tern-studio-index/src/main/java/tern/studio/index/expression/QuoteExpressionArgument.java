package tern.studio.index.expression;

public class QuoteExpressionArgument implements ExpressionArgument {

   private final String value;
   
   public QuoteExpressionArgument(String value) {
      this.value = value;
   }
   
   @Override
   public boolean isExpression() {
      return false;
   }
   
   @Override
   public String getValue() {
      return value;
   }

   @Override
   public Expression getExpression() {
      return null;
   }
   
   @Override
   public String toString() {
      return "\"" + value + "\"";
   }
}
