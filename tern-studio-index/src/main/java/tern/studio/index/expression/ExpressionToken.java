package tern.studio.index.expression;

import java.util.Arrays;
import java.util.List;

public class ExpressionToken {

   private final ExpressionArgument[] arguments;
   private final ExpressionBraceType braces;
   private final String name;
   
   public ExpressionToken(String name, ExpressionArgument[] arguments, ExpressionBraceType braces) {
      this.arguments = arguments;
      this.braces = braces;
      this.name = name;
   }
   
   public ExpressionBraceType getBraces() {
      return braces;
   }
   
   public List<ExpressionArgument> getArguments() {
      return Arrays.asList(arguments);
   }
   
   public String getName() {
      return name;
   }
   
   @Override
   public String toString() {
      return name;
   }
}
