package org.ternlang.studio.index.expression;

public interface ExpressionArgument {
   boolean isExpression();
   Object getValue();
   Expression getExpression();
}
