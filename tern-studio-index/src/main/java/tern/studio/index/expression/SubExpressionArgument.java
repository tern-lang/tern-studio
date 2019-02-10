package tern.studio.index.expression;

public class SubExpressionArgument implements ExpressionArgument {
   
   private final String value;
   
   public SubExpressionArgument(String value) {
      this.value = value;
   }
   
   public boolean isExpression() {
      return true;
   }
   
   public String getValue() {
      return value;
   }
   
   public Expression getExpression() {
      return new ExpressionParser(value);
   }
   
   @Override
   public String toString() {
      return value;
   }
}
