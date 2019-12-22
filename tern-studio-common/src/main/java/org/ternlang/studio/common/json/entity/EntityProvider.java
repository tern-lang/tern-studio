package org.ternlang.studio.common.json.entity;

public interface EntityProvider {
   Object getInstance(CharSequence type);
   Entity getEntity(CharSequence type);
}
