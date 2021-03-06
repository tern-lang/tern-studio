package org.ternlang.studio.agent.local.store;

import java.io.InputStream;
import java.io.OutputStream;

import org.ternlang.common.store.Store;
import org.ternlang.studio.agent.ProcessStore;

public class LocalStore implements ProcessStore {
   
   private final Store store;
   
   public LocalStore(Store store) {
      this.store = store;
   }

   @Override
   public InputStream getInputStream(String path) {
      return store.getInputStream(path);
   }

   @Override
   public OutputStream getOutputStream(String path) {
      return store.getOutputStream(path);
   }

   @Override
   public void update(String project) {}
}
