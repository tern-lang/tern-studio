package org.ternlang.studio.resource.action.write;

import org.ternlang.studio.resource.action.Context;

public interface BodyWriter<T> {
   boolean accept(Context context, Object result) throws Exception;
   void write(Context context, T result) throws Exception;
}
