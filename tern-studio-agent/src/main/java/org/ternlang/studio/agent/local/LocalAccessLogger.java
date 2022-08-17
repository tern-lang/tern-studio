package org.ternlang.studio.agent.local;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class LocalAccessLogger {

   @SuppressWarnings("unchecked")
   public static void disableAccessWarnings() {
      try {
         Class unsafeClass = Class.forName("sun.misc.Unsafe");
         Field field = unsafeClass.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Object unsafe = field.get(null);

         Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
         Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

         Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
         Field loggerField = loggerClass.getDeclaredField("logger");
         Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
         putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
      } catch (Throwable cause) {
      }
   }
}
