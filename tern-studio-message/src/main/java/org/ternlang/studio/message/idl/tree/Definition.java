package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Package;

public interface Definition {
   void define(Scope scope, Package module) throws Exception;
   void process(Scope scope, Package module) throws Exception;
}
