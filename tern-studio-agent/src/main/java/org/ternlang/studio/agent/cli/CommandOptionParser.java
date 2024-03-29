package org.ternlang.studio.agent.cli;

import org.ternlang.core.module.FilePathConverter;
import org.ternlang.core.module.Path;
import org.ternlang.core.module.PathConverter;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class CommandOptionParser {

   private final List<? extends CommandOption> options;
   private final PathConverter converter;
   
   public CommandOptionParser(List<? extends CommandOption> options) {
      this.converter = new FilePathConverter();
      this.options = options;
   }
   
   public CommandValue parse(CommandOption option, String value) {
      int length = value.length();
      
      if(length > 1) {
         String start = value.substring(0, 1);
         
         if(start.equals("\"") || start.equals("\'")) {
            if(value.endsWith(start)) {
               value = value.substring(1, length - 1);
            }
         }
      }
      String name = option.getName();
      Class type = option.getType();
      Object object = convert(value, type);

      return new CommandValue(name, object);
   }
   
   public String interpolate(Object object) {
      if(object != null) {
         String text = String.valueOf(object);
         Map<Object, String> variables = variables(text);
         Set<Object> keys = variables.keySet();

         for(Object key : keys) {
            String value = variables.get(key);

            text = text.replace("$" + key, value);
            text = text.replace("${" + key + "}", value);
         }
         return text;
      }
      return null;
   }

   private Map<Object, String> variables(String text) {
      Map<Object, String> map = new HashMap<Object, String>();

      if (text.contains("$")) {
         Properties properties = System.getProperties();
         Enumeration<?> names = properties.propertyNames();

         while (names.hasMoreElements()) {
            Object name = names.nextElement();
            Object value = properties.get(name);
            String token = String.valueOf(value);

            map.put(name, token);
         }
         Map<String, String> environment = System.getenv();

         if(environment.isEmpty()) {
            map.putAll(environment);
         }
      }
      return map;
   }

   public Object convert(Object object, Class type) {
      try {
         String value = interpolate(object);
         
         if(type == File[].class) {
            StringTokenizer tokenizer = new StringTokenizer(value, File.pathSeparator);
            List<File> files = new ArrayList<File>();
            
            while(tokenizer.hasMoreTokens()) {
               String token = tokenizer.nextToken();
               int length = token.length();
               
               if(length > 0) {
                  File file = new File(token);
                  files.add(file);
               }
            }
            return Collections.unmodifiableList(files);
         }
         if(type == Boolean.class) {
            return Boolean.parseBoolean(value);
         }
         if(type == Integer.class) {
            return Integer.parseInt(value);
         }
         if(type == Long.class) {
            return Long.parseLong(value);
         }
         if(type == URI.class) {
            if(!value.startsWith("http:") && !value.startsWith("https:")) {
               throw new IllegalStateException("Resource '" + value + "' is not a url");
            }
            return new URI(value);
         }
         if(type == File.class) {
            return new File(value);
         }
         if(type == Path.class) {
            return converter.createPath(value);
         }
         if(type.isEnum()) {
            return Enum.valueOf(type, value);
         }
         return value;
      } catch(Exception e) {
         throw new IllegalStateException("Error parsing " + object, e);
      }
   }
}
