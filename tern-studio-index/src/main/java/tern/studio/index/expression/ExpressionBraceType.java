package tern.studio.index.expression;

public enum ExpressionBraceType {
   INVOKE("()"),
   INDEX("[]"),
   ENCLOSE("{}");
   
   public final String braces;
   public final char open;
   public final char close;
   
   private ExpressionBraceType(String braces) {
      this.close = braces.charAt(1);
      this.open = braces.charAt(0);
      this.braces = braces;
   }
   
   public static ExpressionBraceType resolveBraceType(char type) {
      if(type == '(' || type == ')') {
         return ExpressionBraceType.INVOKE;
      }
      if(type == '{' || type == '}') {
         return ExpressionBraceType.ENCLOSE;
      }
      if(type == '[' || type == ']') {
         return ExpressionBraceType.INDEX;
      }
      return null;
   }
   
   public static boolean isOpenBrace(char value) {
      switch(value){
      case '(': case '[':
      case '{':
         return true;
      }
      return false; 
   }
   
   public static boolean isCloseBrace(char value) {
      switch(value){
      case ')': case ']':
      case '}':
         return true;
      }
      return false;
   }
}
