package org.ternlang.studio.core.image.loader;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class SandBoxLauncher {

   public static void main(String[] list) throws Exception {
      URI classes = URI.create(list[0]);
      String type = list[1];
      String prefix = list[2];
      String[] arguments = Arrays.copyOfRange(list, 3, list.length);
      
      start(classes, type, prefix, arguments);
   }

   public static void start(URI classes, String name, String prefix, String[] arguments) throws Exception {
      ClassLoader parent = ClassLoader.getSystemClassLoader();
      URL[] path = new URL[]{classes.toURL()};
      URLClassLoader loader = new SandBoxClassLoader(path, parent, prefix);
      Class type = loader.loadClass(name);
      Method method = type.getDeclaredMethod("main", String[].class);

      //Thread.currentThread().setContextClassLoader(loader); // for some reason this does not work
      method.invoke(null, (Object)arguments);
   }
}      