package org.ternlang.studio.resource.action.build;

import java.util.Collections;
import java.util.List;

import org.ternlang.studio.resource.action.Context;

public class EmptyResolver implements MethodResolver {

   @Override
   public MethodDispatcher resolveBest(Context context) throws Exception {
      return null;
   }

   @Override
   public List<MethodDispatcher> resolveBestFirst(Context context) throws Exception {
      return Collections.emptyList();
   }

   @Override
   public List<MethodDispatcher> resolveBestLast(Context context) throws Exception {
      return Collections.emptyList();
   }

}
