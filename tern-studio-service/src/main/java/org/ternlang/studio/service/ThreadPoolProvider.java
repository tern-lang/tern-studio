package org.ternlang.studio.service;

import org.ternlang.common.thread.ThreadPool;
import org.ternlang.core.Bug;
import org.ternlang.studio.resource.action.annotation.Component;

@Bug("Should be @Provider")
@Component
public class ThreadPoolProvider extends ThreadPool {
   
   public ThreadPoolProvider() {
      super(10);
   }

   public ThreadPool getPool() {
      return new ThreadPool(10);
   }
}
