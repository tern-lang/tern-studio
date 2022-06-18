package org.ternlang.studio;

import static org.ternlang.studio.StudioServiceBuilder.ServiceType.TERM;
import static org.ternlang.studio.common.SessionCookie.SESSION_ID;

import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.resource.container.ServerDriver;

@Module
@Import(StudioTerminal.class)
public class StudioTerminal {

   public static void main(String[] list) throws Exception {
      StudioServiceBuilder builder = new StudioServiceBuilder(list, TERM);
      StudioService service = builder.create();
      service.getCommandLine();

      Application.create(ServerDriver.class)
           .path("..")
           .file("term")
           .file("terminal")
           .register(StudioTerminal.class)
           .create(list)
           .name("Apache/2.2.14")
           .session(SESSION_ID)
           .threads(2)
           .start();
   }
}
