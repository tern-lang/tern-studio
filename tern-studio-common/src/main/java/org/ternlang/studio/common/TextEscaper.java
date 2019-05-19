package org.ternlang.studio.common;

public class TextEscaper {

   public static String escape(byte[] array) throws Exception{
      return escape(array, 0, array.length);
   }
   
   public static String escape(byte[] array, int offset, int length) throws Exception {
      String text = new String(array, offset, length, "UTF-8");
      return escape(text);
   }
   
   public static String escape(String text) throws Exception {
      int length = text.length();

      if (length > 0) {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
               builder.append("&#");
               builder.append((int)c);
               builder.append(';');
            } else {
               builder.append(c);
            }
         }
         return builder.toString();

      }
      return text;
   }
}