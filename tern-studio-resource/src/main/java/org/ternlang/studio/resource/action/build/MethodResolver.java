package org.ternlang.studio.resource.action.build;

import java.util.List;

import org.ternlang.studio.resource.action.Context;

public interface MethodResolver {
   MethodDispatcher resolveBest(Context context) throws Exception;
   List<MethodDispatcher> resolveBestFirst(Context context) throws Exception;
   List<MethodDispatcher> resolveBestLast(Context context) throws Exception;
}
