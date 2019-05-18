package org.ternlang.studio.common.display;

import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.springframework.stereotype.Component;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;

import com.google.gson.Gson;

// /theme/<project>
@Component
@ResourcePath("/display/.*")
public class DisplayThemeResource implements Resource {
   
   private final DisplayPersister displayPersister;
   private final Gson gson;
   
   public DisplayThemeResource(DisplayPersister displayPersister) {
      this.displayPersister = displayPersister;
      this.gson = new Gson();
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      DisplayDefinition display = displayPersister.readDefinition();
      PrintStream out = response.getPrintStream();
      String text = gson.toJson(display);
      response.setStatus(Status.OK);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }

}