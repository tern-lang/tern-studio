package org.ternlang.studio.common;

import org.simpleframework.http.Request;

public class RequestParser {

   private final Request request;
   
   public RequestParser(Request request) {
      this.request = request;
   }
   
   public String getString(String name) {
      return getString(name, true);
   }
   
   public String getString(String name, boolean required) {
      String value = request.getParameter(name);
      
      if(value == null && required) {
         throw new IllegalArgumentException("Could not find parameter " + name);
      }
      return value;
   }
   
   public String getString(String name, String other) {
      String value = request.getParameter(name);
      
      if(value == null) {
         return other;
      }
      return value;
   }
   
   public boolean getBoolean(String name) {
      String value = getString(name);
      return Boolean.parseBoolean(value);
   }
   
   public boolean getBoolean(String name, boolean required) {
      String value = getString(name, required);
      
      if(value != null) {
         return Boolean.parseBoolean(value);
      }
      return false;
   }
   
   public int getInteger(String name) {
      String value = getString(name);
      return Integer.parseInt(value);
   }
   
   public int getInteger(String name, boolean required) {
      String value = getString(name, required);
      
      if(value == null) {
         return Integer.parseInt(value);
      }
      return 0;
   }
   
   public double getDouble(String name) {
      String value = getString(name);
      return Double.parseDouble(value);
   }
   
   public double getDouble(String name, boolean required) {
      String value = getString(name, required);
      
      if(value != null) {
         return Double.parseDouble(value);
      }
      return Double.NaN;
   }
}