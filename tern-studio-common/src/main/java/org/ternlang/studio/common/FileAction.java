package org.ternlang.studio.common;

import java.io.File;

public interface FileAction<T> {
   T execute(String reference, File file) throws Exception;
}