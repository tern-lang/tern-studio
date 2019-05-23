package org.ternlang.studio;

import org.simpleframework.module.ApplicationBuilder;
import org.simpleframework.module.resource.ResourceServiceBuilder;
import org.ternlang.studio.core.StudioCommandLine;

import lombok.SneakyThrows;

public class StudioApplicationBuilder implements ApplicationBuilder<StudioService> {

   private ResourceServiceBuilder builder;
   private String[] arguments;
   
   public StudioApplicationBuilder() {
      this.builder = new ResourceServiceBuilder();
      this.arguments = new String[]{};
   }
   
   @Override
   public StudioApplicationBuilder register(Class<?> type) {
      builder.register(type);
      return this;
   }
   
   public StudioApplicationBuilder arguments(String[] arguments) {
      this.arguments = arguments;
      return this;
   }
   
   @Override
   @SneakyThrows
   public StudioService start() {
      StudioService process = new StudioServiceBuilder(arguments).create();
      StudioCommandLine commandLine = process.getCommandLine();
      int port = commandLine.getPort();
      
      builder.listen(port);
      builder.start();

      return process;
   }
}
