package org.ternlang.studio.core.loader;

import java.io.File;
import java.io.OutputStream;
import java.util.jar.Attributes.Name;

public interface JarFile {
   JarFile addManifestAttribute(Name name, String value) throws Exception;
   JarFile addManifestAttribute(String name, String value) throws Exception;
   JarFile addResource(String resource) throws Exception;
   JarFile addResource(Class resource) throws Exception;
   JarFile saveFile(OutputStream stream) throws Exception;
   JarFile saveFile(File file) throws Exception;
}