package org.ternlang.studio.service;

import org.ternlang.common.thread.ThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@org.ternlang.studio.resource.action.annotation.Component
@Configuration
public class ThreadPoolProvider extends ThreadPool {
   
   public ThreadPoolProvider() {
      super(10);
   }

   @Bean
   public ThreadPool getPool() {
      return new ThreadPool(10);
   }
}
