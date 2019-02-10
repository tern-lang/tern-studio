package tern.studio.index;

import tern.core.module.Path;

public interface Index {
   IndexType getType();
   Object getOperation();
   String getConstraint();
   String getName();
   String getModule();
   Path getPath();
   int getLine();
   
}
