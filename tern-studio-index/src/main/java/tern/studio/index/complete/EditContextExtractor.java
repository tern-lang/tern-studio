package tern.studio.index.complete;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditContextExtractor {
   
   private static final char[] TOKEN = "){}".toCharArray();

   public static List<EditContext> extractContext(CompletionRequest request) {
      String source = request.getSource();
      String completion = request.getComplete();
      String lines[] = cleanSource(source);
      int length = completion.length();
      int line = request.getLine();

      if(length > 0) {
         if (lines.length < line) {
            String[] copy = new String[line];

            for (int i = 0; i < lines.length; i++) {
               copy[i] = lines[i];
            }
            for (int i = lines.length; i < line; i++) {
               copy[i] = "";
            }
            lines = copy;
         }
         lines[line - 1] = completion; // insert expression at line
         String result = InputExpressionParser.parseLine(lines, line);

         return Arrays.asList(
                 createContext(request, lines, result, ""),
                 createContext(request, lines, result, ";"));
      }
      return Collections.singletonList(
              createContext(request, lines, "", ""));
   }

   private static EditContext createContext(CompletionRequest request, String[] lines, String result, String replace) {
      String source = request.getSource();
      String completion = request.getComplete();
      int length = completion.length();

      if(length > 0) {
         int line = request.getLine();
         String finished = generateSource(lines, replace, line);
         char last = completion.charAt(length - 1);

         if (Character.isWhitespace(last)) { // did user input end in a space?
            return new EditContext(finished, completion, result + " ");
         }
         return new EditContext(finished, completion, result);
      }
      return new EditContext(source, completion, "");
   }
   

   private static String[] cleanSource(String source) {
      char[] array = source.toCharArray();
      CommentStripper cleaner = new CommentStripper(array);
      String clean = cleaner.clean();
      
      return clean.split("\\r?\\n");
   }
   
   private static String generateSource(String[] lines, String replace, int index) {
      StringBuilder builder = new StringBuilder();
      
      lines[index -1] = replace;
      
      for(String entry : lines) {
         builder.append(entry);
         builder.append("\n");
      }
      String source = builder.toString();
      String trim = source.trim();
      
      if(!trim.isEmpty()) {
         char[] array = source.toCharArray();
         int seek = 0;
         
         builder.setLength(0);
         
         for(int i = 0; i < array.length; i++) {
            char next = array[i];
            
            if(!Character.isWhitespace(next)) {
               if(next != TOKEN[seek++]) {
                  seek = 0;
               }
               if(seek >= TOKEN.length) {
                  char prev = array[i-1];
                  
                  if(Character.isWhitespace(prev)) {
                     builder.append(";"); // add a no-op statement
                  }
                  seek = 0;
               }
            }
            builder.append(next);
         }
         return builder.toString();
      }
      return "println();";
   }
}
