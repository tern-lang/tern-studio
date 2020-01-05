package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Property;

public interface Constraint {
   void process(Scope scope, Property property) throws Exception;
}
