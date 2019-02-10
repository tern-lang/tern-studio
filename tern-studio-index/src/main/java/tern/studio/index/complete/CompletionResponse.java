package tern.studio.index.complete;

import java.util.Collections;
import java.util.Map;

public class CompletionResponse {

   private Map<String, String> tokens;
   private String expression;
   private String details;
   
   public CompletionResponse() {
      this(Collections.EMPTY_MAP, null, null);
   }
   
   public CompletionResponse(Map<String, String> tokens, String expression, String details) {
      this.expression = expression;
      this.tokens = tokens;
      this.details = details;
   }
   
   public String getExpression() {
      return expression;
   }

   public void setExpression(String expression) {
      this.expression = expression;
   }

   public String getDetails() {
      return details;
   }

   public void setDetails(String details) {
      this.details = details;
   }

   public Map<String, String> getTokens() {
      return tokens;
   }

   public void setTokens(Map<String, String> tokens) {
      this.tokens = tokens;
   }
}