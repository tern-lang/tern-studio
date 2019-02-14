package org.ternlang.studio.agent.debug;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ternlang.core.Context;
import org.ternlang.core.ExpressionEvaluator;
import org.ternlang.core.function.Function;
import org.ternlang.core.scope.Scope;

public class ExpressionProcessor {
   
   private static final Object NULL_VALUE = new Object();

   private final Map<String, Object> results; // holds only one expression
   private final Context context;
   private final Scope scope;
   
   public ExpressionProcessor(Context context, Scope scope, Function function) {
      this.results = new ConcurrentHashMap<String, Object>();
      this.context = context;
      this.scope = scope;
   }
   
   public Object evaluate(String expression) {
      return evaluate(expression, false);
   }
   
   public Object evaluate(String expression, boolean refresh) {
      if(refresh) {
         results.clear();
      }
      if(!accept(expression)) {
         results.clear();
         return null;
      }
      if(!results.containsKey(expression)) { // only evaluate once
         Object result = execute(expression);
         
         results.clear(); // clear all expression when changed
         results.put(expression, result); // represents null
      }
      Object result = results.get(expression);
      
      if(result != NULL_VALUE) {
         return result;
      }
      return null;
   }
   
   private Object execute(String expression) {
      try {
         ExpressionEvaluator evaluator = context.getEvaluator();
         Object result =  evaluator.evaluate(scope, expression);

         if(result == null) {
            return NULL_VALUE; // this is a special 'null' value
         } 
         return result;
      } catch(Exception cause) {
         cause.printStackTrace();
         return cause;
      }
   }
   
   private boolean accept(String expression) {
      if(expression != null) {
         String token = expression.trim();
         int length = token.length();
         
         if(length > 0) {
            return true; 
         }
      }
      return false;        
   }
}