package org.ternlang.studio.common;

import java.io.File;

import org.simpleframework.http.Path;

public interface FileDirectorySource {
   FileDirectory getByName(String path); // getProject
   FileDirectory getByPath(Path path); // getProject
   File createFile(String name);
}
