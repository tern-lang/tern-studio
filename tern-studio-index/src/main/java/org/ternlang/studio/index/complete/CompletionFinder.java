package org.ternlang.studio.index.complete;

import java.util.Set;

import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexNode;

public interface CompletionFinder {
   InputExpression parseExpression(EditContext context);
   Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, InputExpression text) throws Exception;
}
