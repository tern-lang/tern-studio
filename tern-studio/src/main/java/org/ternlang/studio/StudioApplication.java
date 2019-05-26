package org.ternlang.studio;

import org.simpleframework.module.ApplicationBuilder;
import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.resource.ServerApplication;

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
         .withCookie("SESSID")
         .withThreads(10)
         .start();

   }

}
