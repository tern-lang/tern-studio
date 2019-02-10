package tern.studio.index.complete;

public class SourceCodeInterpolator {
   
   public static final String SOURCE_TO_REPLACE = "// replace me";
   
   public static CompletionRequest buildRequest(String source, String expression) {
      StringBuilder builder = new StringBuilder();
      CompletionRequest request = new CompletionRequest();
      String lines[] = source.split("\\r?\\n");
      int line = -1;
      
      for(int i = 0; i < lines.length; i++){
         String entry = lines[i];
      
         if(entry.contains(SOURCE_TO_REPLACE)) {
            builder.append("");
            line = i + 1;
         } else {
            builder.append(entry);
         }
         builder.append("\n");
      }
      if(line == -1) {
         throw new IllegalStateException("Could not find " + SOURCE_TO_REPLACE);
      }
      String formatted = builder.toString();
      
      request.setComplete(expression);
      request.setLine(line);
      request.setResource("/some/resource.snap");
      request.setSource(formatted);
      
      return request;
   }
}
