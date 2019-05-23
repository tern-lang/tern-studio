package org.ternlang.studio;

import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;

@Module
@Import(StudioApplication.class)
public class StudioApplication {

   public static void main(String[] list) throws Exception {
      Application.create(StudioApplicationBuilder.class)
         .register(StudioApplication.class)
         .arguments(list)
         .start();
   }


}
