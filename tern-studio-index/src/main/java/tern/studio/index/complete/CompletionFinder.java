package tern.studio.index.complete;

import java.util.Set;

import tern.studio.index.IndexDatabase;
import tern.studio.index.IndexNode;

public interface CompletionFinder {
   InputExpression parseExpression(EditContext context);
   Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, InputExpression text) throws Exception;
}
