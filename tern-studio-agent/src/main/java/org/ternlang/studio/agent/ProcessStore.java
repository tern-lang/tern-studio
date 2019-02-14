package org.ternlang.studio.agent;

import org.ternlang.common.store.Store;

public interface ProcessStore extends Store{
   void update(String project);
}
