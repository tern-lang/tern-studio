package tern.studio.service;

import tern.common.thread.ThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolProvider {

   @Bean
   public ThreadPool getPool() {
      return new ThreadPool(10);
   }
}
