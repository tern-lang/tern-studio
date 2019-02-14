package org.ternlang.studio.index.complete;

import java.util.Collections;
import java.util.Map;

public class CompletionOutlineResponse {

   private Map<String, CompletionOutline> outlines;
   private String expression;
   private String details;
   
   public CompletionOutlineResponse() {
      this(Collections.EMPTY_MAP, null, null);
   }
   
   public CompletionOutlineResponse(Map<String, CompletionOutline> outlines, String expression, String details) {
      this.expression = expression;
      this.outlines = outlines;
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

   public Map<String, CompletionOutline> getOutlines() {
      return outlines;
   }

   public void setOutlines(Map<String, CompletionOutline> outlines) {
      this.outlines = outlines;
   }
}