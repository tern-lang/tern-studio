package tern.studio.agent;

import tern.common.store.Store;

public interface ProcessStore extends Store{
   void update(String project);
}
