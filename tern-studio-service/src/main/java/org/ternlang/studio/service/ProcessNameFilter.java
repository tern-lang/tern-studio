package org.ternlang.studio.service;

public interface ProcessNameFilter {
   String generate();
   boolean accept(String name);
}
