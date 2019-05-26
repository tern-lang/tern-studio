package org.ternlang.studio;

import static org.simpleframework.module.resource.SessionCookie.SESSION_ID;

import org.simpleframework.module.ApplicationBuilder;
import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.resource.server.ServerApplication;

@Module
@Import(StudioApplication.class)
public class StudioApplication {

   public static void main(String[] list) throws Exception {
      StudioServiceBuilder builder = new StudioServiceBuilder(list);
      StudioService service = builder.create();
      service.getCommandLine();

      ApplicationBuilder.create(ServerApplication.class)
         .withPath("ternd")
         .withPath("tern-studio")
         .withModule(StudioApplication.class)
         .withArguments(list)
         .withName("Apache/2.2.14")
         .withCookie(SESSION_ID)
         .withThreads(10)
         .start();

   }

}
