package org.ternlang.studio.core.complete;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.module.annotation.Component;
import org.ternlang.studio.project.Project;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SourceFormatter {

   private static final String JSON_EXTENSION = ".json";
   private static final String XML_EXTENSION = ".xml";

   private final PrettyPrinter printer;
   private final ObjectMapper mapper;

   public SourceFormatter(){
      this.printer = new DefaultPrettyPrinter();
      this.mapper = new ObjectMapper();
   }
   
   public String format(Project project, String path, String source, int indent) throws Exception {
      String resource = path.toLowerCase();
      
      if(resource.endsWith(JSON_EXTENSION)) {
         Object object = mapper.readValue(source, Object.class);
         return mapper.writer(printer).writeValueAsString(object);
      }
      Pattern pattern = Pattern.compile("^(\\s+)(.*)$");
      String lines[] = source.split("\\r?\\n");
      String pad = "";
      
      for(int i = 0; i < indent; i++) {
         pad += " ";
      }
      if(lines.length > 0){
         StringBuilder builder = new StringBuilder();
         
         for(String line : lines) {
            Matcher matcher = pattern.matcher(line);
            
            if(matcher.matches()) {
               String prefix = matcher.group(1);
               String remainder = matcher.group(2);
               float length = prefix.length();
               float factor = length / indent;
               
               if(length > 0) {
                  int count = Math.round(factor);
                  
                  for(int i = 0; i < count; i++) {
                     builder.append(pad);
                  }
                  builder.append(remainder);
               } else {
                  builder.append(remainder);
               }
            }else {
               builder.append(line);
            }
            builder.append("\n");
         }
         return builder.toString();
      }
      return source;
   }
}