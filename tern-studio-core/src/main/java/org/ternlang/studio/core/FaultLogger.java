package org.ternlang.studio.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class FaultLogger {
   
   private static final String INDENT = "   ";

   public void log(String process, Map<String, Map<String, String>> local, String resource, String thread, String cause, int line) {
      String description = createDescription(resource, thread, line);
      String variables = createVariables(local, INDENT);
      String exception = createException(cause, INDENT);
   
      log.debug(description + exception + variables);
   }
   
   private String createDescription(String resource, String thread, int line) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      writer.print("ERROR: '");
      writer.print(resource);
      writer.print("' at line ");
      writer.print(line);
      writer.print(" on thread '");
      writer.print(thread);
      writer.println("'");
      writer.flush();
      
      return builder.toString();
   }
   
   private String createVariables(Map<String, Map<String, String>> variables, String indent) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      if(!variables.isEmpty()) {
         Set<String> names = variables.keySet();
         
         for(String name : names) {
            Map<String, String> data = variables.get(name);
            Set<String> keys = data.keySet();
    
            writer.print(indent);
            writer.print("name: ");
            writer.print(name);
            writer.println();
            
            for(String key : keys) {
               String value = data.get(key);
            
               writer.print(indent);
               writer.print(indent);
               writer.print(key);
               writer.print(": ");
               writer.print(value);
               writer.println();
            }
         }
         writer.flush();
      }
      return builder.toString();
   }
   
   private String createException(String cause, String indent) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      String[] lines = cause.split("\\r?\\n");
      StringBuffer buffer = builder.getBuffer();
      
      buffer.setLength(0);
      writer.print(indent);
      writer.print("cause: ");
      
      for(int i = 0; i < lines.length; i++) {
         String line = lines[i];
         
         if(i > 0) {
            writer.print(indent);
            writer.print(indent);
         }
         writer.println(line);
      }
      writer.flush();
      writer.close();
      
      return builder.toString();
   }
}