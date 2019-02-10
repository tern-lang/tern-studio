package tern.studio.index.expression;

public class BooleanExpressionArgument  implements ExpressionArgument {

   private final String value;
   
   public BooleanExpressionArgument(String value) {
      this.value = value;
   }
   
   @Override
   public boolean isExpression() {
      return false;
   }
   
   @Override
   public Object getValue() {
      return Boolean.parseBoolean(value);
   }

   @Override
   public Expression getExpression() {
      return null;
   }
   
   @Override
   public String toString() {
      return value;
   }
}