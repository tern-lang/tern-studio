package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Property;

public interface Constraint {
   void define(Scope scope, Property property) throws Exception;
   void process(Scope scope, Property property) throws Exception;
}
