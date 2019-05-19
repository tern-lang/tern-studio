package org.ternlang.studio.core;

public interface ProcessNameFilter {
   String generate();
   boolean accept(String name);
}
