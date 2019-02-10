package tern.studio.service.complete;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tern.studio.project.Project;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
         ObjectWriter writer = mapper.writer(printer);
         
         return writer.writeValueAsString(object);
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