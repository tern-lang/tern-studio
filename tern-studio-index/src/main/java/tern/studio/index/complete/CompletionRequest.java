package tern.studio.index.complete;

public class CompletionRequest {

   private String resource; // name of file
   private String source; // source code of file from edit buffer
   private String complete; // the text expression to complete
   private int line; // the line the expression is on
   
   public CompletionRequest() {
      super();
   }

   public int getLine() {
      return line;
   }

   public void setLine(int line) {
      this.line = line;
   }

   public String getComplete() {
      return complete;
   }
   
   public void setComplete(String complete) {
      this.complete = complete;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }
}