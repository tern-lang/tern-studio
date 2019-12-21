package org.ternlang.studio.service.json.handler;

import org.ternlang.studio.service.json.operation.Type;

public interface DocumentBuilder {
   Document create();
   Document create(Type type);
}
