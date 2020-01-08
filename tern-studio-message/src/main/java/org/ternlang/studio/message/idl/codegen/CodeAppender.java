package org.ternlang.studio.message.idl.codegen;

public class CodeAppender {

   private final StringBuilder builder;
   
   public CodeAppender() {
      this.builder = new StringBuilder();
   }
   
   public CodeAppender append(String text) {
      builder.append(text);
      return this;
   }
   
   public CodeAppender append(String text, Object... values) {
      builder.append(String.format(text, values));
      return this;
   }
   
   public CodeAppender reset() {
      builder.setLength(0);
      return this;
   }
   
   public String toString() {
      return builder.toString();
   }
}
