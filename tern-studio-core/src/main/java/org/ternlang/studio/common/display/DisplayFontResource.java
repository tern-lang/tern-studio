package org.ternlang.studio.common.display;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.springframework.stereotype.Component;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;

// /theme/<project>
@Component
@ResourcePath("/font/.*")
public class DisplayFontResource implements Resource {

   private final DisplayPersister displayPersister;
   private final Gson gson;

   public DisplayFontResource(DisplayPersister displayPersister) {
      this.displayPersister = displayPersister;
      this.gson = new Gson();
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      DisplayDefinition display = displayPersister.readDefinition();
      Map<String, String> availableFonts = display.getAvailableFonts();
      PrintStream out = response.getPrintStream();
      Set<String> styles = availableFonts.keySet();

      response.setStatus(Status.OK);
      response.setContentType("text/css");

      for(String style : styles) {
         String name = availableFonts.get(style);
         String path = name.replace(" ", "");

         out.println("@font-face {");
         out.println("  font-family: '" + name + "';");
         out.println("  src: url('/ttf/" + path + ".ttf') format('truetype');");
         out.println("}");
         out.println();
      }
      out.close();
   }

}