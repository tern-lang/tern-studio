package org.ternlang.studio;

import static org.ternlang.studio.common.SessionCookie.SESSION_ID;

import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.resource.container.ServerDriver;

@Module
@Import(StudioApplication.class)
public class StudioApplication {

   public static void main(String[] list) throws Exception {
      StudioServiceBuilder builder = new StudioServiceBuilder(list);
      StudioService service = builder.create();
      service.getCommandLine();

      Application.create(ServerDriver.class)
         .path("..")
         .file("ternd")
         .file("tern-studio")
         .register(StudioApplication.class)
         .create(list)
         .name("Apache/2.2.14")
         .session(SESSION_ID)
         .threads(10)
         .start();
   }

}
