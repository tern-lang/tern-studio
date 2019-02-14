package org.ternlang.studio.index;

import org.ternlang.core.module.Path;

public interface Index {
   IndexType getType();
   Object getOperation();
   String getConstraint();
   String getName();
   String getModule();
   Path getPath();
   int getLine();
   
}
