package tern.studio.project.config;

import org.simpleframework.xml.filter.Filter;

public class ConfigurationFilter implements Filter {

   @Override
   public String replace(String text) {
      String value = System.getenv(text);
      
      if(value == null) {
         value = System.getProperty(text);
      }
      return value;
   }
}