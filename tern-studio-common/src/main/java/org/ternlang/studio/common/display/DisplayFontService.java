package org.ternlang.studio.common.display;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import org.simpleframework.module.annotation.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DisplayFontService {

   private final DisplayPersister persister;

   public String style() {
      DisplayDefinition display = persister.readDefinition();
      Map<String, String> fonts = display.getAvailableFonts();
      Set<String> styles = fonts.keySet();
      
      if(!styles.isEmpty()) {
         StringWriter builder = new StringWriter();
         PrintWriter writer = new PrintWriter(builder);
   
         for(String style : styles) {
            String name = fonts.get(style);
            String path = name.replace(" ", "");
   
            writer.println("@font-face {");
            writer.println("  font-family: '" + name + "';");
            writer.println("  src: url('/ttf/" + path + ".ttf') format('truetype');");
            writer.println("}");
            writer.println();
         }
         writer.close();
         return builder.toString();
      }
      return "/* no fonts defined */";
   }

}