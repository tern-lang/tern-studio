package org.ternlang.studio.resource.action.extract;

import org.ternlang.studio.resource.action.Context;

public interface Extractor<T> {
   T extract(Parameter parameter, Context context) throws Exception;
   boolean accept(Parameter parameter) throws Exception;
}
